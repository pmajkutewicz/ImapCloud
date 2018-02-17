package pl.pamsoft.imapcloud.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.resume.ExistingChunkFilter;
import pl.pamsoft.imapcloud.services.upload.ChunkEncrypter;
import pl.pamsoft.imapcloud.services.upload.ChunkUploaderFacade;
import pl.pamsoft.imapcloud.services.upload.DirectoryProcessor;
import pl.pamsoft.imapcloud.services.upload.DirectorySizeCalculator;
import pl.pamsoft.imapcloud.services.upload.FileChunkStorer;
import pl.pamsoft.imapcloud.services.upload.FileSplitter;
import pl.pamsoft.imapcloud.services.upload.FileStorer;
import pl.pamsoft.imapcloud.services.upload.FileWithProgressBinder;
import pl.pamsoft.imapcloud.services.upload.UploadChunkHasher;
import pl.pamsoft.imapcloud.services.upload.UploadFileHasher;
import pl.pamsoft.imapcloud.utils.GitStatsUtil;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

@Service
public class UploadService extends AbstractBackgroundService {

	@Autowired
	private AccountServicesHolder accountServicesHolder;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private FileServices fileServices;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private GitStatsUtil gitStatsUtil;

	@SuppressFBWarnings("STT_TOSTRING_STORED_IN_FIELD")
	public boolean resume(File fileByUniqueId) throws RejectedExecutionException {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> future = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("UT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			Account account = fileByUniqueId.getOwnerAccount();
			AccountService accountService = accountServicesHolder.getAccountService(account.getType());
			getTaskProgressMap().put(taskId, getTasksProgressService().create(TaskType.UPLOAD, taskId, fileByUniqueId.getSize(),
				singletonList(new FileDto(fileByUniqueId.getName(), fileByUniqueId.getAbsolutePath(), FileDto.FileType.FILE, fileByUniqueId.getSize()))));

			Function<File, UploadChunkContainer> packInContainer = file -> {
				FileDto fileDto = new FileDto(file.getName(), file.getAbsolutePath(), FileDto.FileType.FILE, file.getSize());
				UploadChunkContainer ucc = new UploadChunkContainer(taskId, fileDto, fileByUniqueId.isChunkEncryptionEnabled());
				ucc = UploadChunkContainer.addFileHash(ucc, file.getFileHash());
				return UploadChunkContainer.addIds(ucc, file.getId(), file.getFileUniqueId());
			};
			Function<UploadChunkContainer, Stream<UploadChunkContainer>> splitFileIntoChunks = new FileSplitter(account.getAttachmentSizeMB(), 2, fileServices, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> generateChunkHash = new UploadChunkHasher(getMonitoringHelper());
			Predicate<UploadChunkContainer> existingChunkFilter = new ExistingChunkFilter(fileByUniqueId.getFileUniqueId(), fileServices);

			Predicate<UploadChunkContainer> filterEmptyUcc = ucc -> UploadChunkContainer.EMPTY != ucc;
			Function<UploadChunkContainer, UploadChunkContainer> chunkEncrypter = new ChunkEncrypter(cryptoService, account.getCryptoKey(), getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> chunkUploader = new ChunkUploaderFacade(accountService.getChunkUploader(account), cryptoService, account.getCryptoKey(), gitStatsUtil, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> storeFileChunk = new FileChunkStorer(fileServices);
			Consumer<UploadChunkContainer> updateProgress = ucc -> getTaskProgressMap().get(ucc.getTaskId())
				.process(ucc.getFileDto().getAbsolutePath(), ucc.getCurrentFileChunkCumulativeSize());
			Consumer<UploadChunkContainer> persistTaskProgress = ucc -> {
				TaskProgress updated = getTasksProgressService().persist(getTaskProgressMap().get(ucc.getTaskId()));
				getTaskProgressMap().put(ucc.getTaskId(), updated);
			};

			Stream.of(fileByUniqueId)
				.map(packInContainer)
				.flatMap(splitFileIntoChunks)
				.map(generateChunkHash)
				.filter(existingChunkFilter)
				.map(ucc -> ucc.isChunkEncryptionEnabled() ? chunkEncrypter.apply(ucc) : ucc)
				.filter(filterEmptyUcc)
				.map(chunkUploader)
				.filter(filterEmptyUcc)
				.map(storeFileChunk)
				.peek(updateProgress)
				.peek(persistTaskProgress)
				.forEach(System.out::println);
		});
		getTaskMap().put(taskId, future);
		return true;
	};

	@SuppressFBWarnings("STT_TOSTRING_STORED_IN_FIELD")
	public boolean upload(AccountDto selectedAccount, List<FileDto> selectedFiles, boolean chunkEncryptionEnabled) throws RejectedExecutionException {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> future = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("UT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			final Account account = accountRepository.getById(selectedAccount.getId());
			AccountService accountService = accountServicesHolder.getAccountService(account.getType());
			final Long bytesToProcess = new DirectorySizeCalculator(filesIOService, getMonitoringHelper()).apply(selectedFiles);
			getTaskProgressMap().put(taskId, getTasksProgressService().create(TaskType.UPLOAD, taskId, bytesToProcess, selectedFiles));

			Predicate<UploadChunkContainer> filterEmptyUcc = ucc -> UploadChunkContainer.EMPTY != ucc;
			Consumer<UploadChunkContainer> updateProgress = ucc -> getTaskProgressMap().get(ucc.getTaskId())
				.process(ucc.getFileDto().getAbsolutePath(), ucc.getCurrentFileChunkCumulativeSize());
			Consumer<UploadChunkContainer> markFileAlreadyUploaded = ucc -> getTaskProgressMap().get(ucc.getTaskId())
				.markFileAlreadyUploaded(ucc.getFileDto().getAbsolutePath(), ucc.getFileDto().getSize());

			Consumer<UploadChunkContainer> persistTaskProgress = ucc -> {
				TaskProgress updated = getTasksProgressService().persist(getTaskProgressMap().get(ucc.getTaskId()));
				getTaskProgressMap().put(ucc.getTaskId(), updated);
			};

			Function<FileDto, UploadChunkContainer> packInContainer = fileDto -> new UploadChunkContainer(taskId, fileDto, chunkEncryptionEnabled);
			Function<UploadChunkContainer, Stream<UploadChunkContainer>> parseDirectories = new DirectoryProcessor(filesIOService, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> generateFilehash = new UploadFileHasher(filesIOService, getMonitoringHelper());
			Predicate<UploadChunkContainer> removeFilesWithSize0 = ucc -> ucc.getFileDto().getSize() > 0;
			Function<UploadChunkContainer, UploadChunkContainer> storeFile = new FileStorer(fileServices, account, markFileAlreadyUploaded.andThen(persistTaskProgress));
			Consumer<UploadChunkContainer> fileWithProgressBinder = new FileWithProgressBinder(fileServices, getTaskProgressMap());
			Function<UploadChunkContainer, Stream<UploadChunkContainer>> splitFileIntoChunks = new FileSplitter(account.getAttachmentSizeMB(), 2, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> generateChunkHash = new UploadChunkHasher(getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> chunkEncrypter = new ChunkEncrypter(cryptoService, account.getCryptoKey(), getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> chunkUploader = new ChunkUploaderFacade(accountService.getChunkUploader(account), cryptoService, account.getCryptoKey(), gitStatsUtil, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> storeFileChunk = new FileChunkStorer(fileServices);

			selectedFiles.stream()
				.map(packInContainer)
				.flatMap(parseDirectories)
				.map(generateFilehash)
				.filter(removeFilesWithSize0)
				.map(storeFile)
				.peek(fileWithProgressBinder)
				.filter(filterEmptyUcc)
				.flatMap(splitFileIntoChunks)
				.filter(filterEmptyUcc)
				.map(generateChunkHash)
				.map(ucc -> chunkEncryptionEnabled ? chunkEncrypter.apply(ucc) : ucc)
				.filter(filterEmptyUcc)
				.map(chunkUploader)
				.filter(filterEmptyUcc)
				.map(storeFileChunk)
				.peek(updateProgress)
				.peek(persistTaskProgress)
				.forEach(System.out::println);
		});
		getTaskMap().put(taskId, future);
		return true;
	}

	protected int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	protected String getNameFormat() {
		return "UploadTask-%d";
	}

}
