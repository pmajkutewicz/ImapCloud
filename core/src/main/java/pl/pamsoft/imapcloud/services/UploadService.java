package pl.pamsoft.imapcloud.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.imap.ChunkSaver;
import pl.pamsoft.imapcloud.services.common.TasksProgressService;
import pl.pamsoft.imapcloud.services.upload.ChunkEncrypter;
import pl.pamsoft.imapcloud.services.upload.DirectoryProcessor;
import pl.pamsoft.imapcloud.services.upload.DirectorySizeCalculator;
import pl.pamsoft.imapcloud.services.upload.FileChunkStorer;
import pl.pamsoft.imapcloud.services.upload.FileSplitter;
import pl.pamsoft.imapcloud.services.upload.FileStorer;
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

@Service
public class UploadService extends AbstractBackgroundService {

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private FileServices fileServices;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private TasksProgressService tasksProgressService;

	@Autowired
	private GitStatsUtil gitStatsUtil;

	@SuppressFBWarnings("STT_TOSTRING_STORED_IN_FIELD")
	public boolean upload(AccountDto selectedAccount, List<FileDto> selectedFiles, boolean chunkEncryptionEnabled) throws RejectedExecutionException {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> future = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("UT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			final Account account = accountRepository.getById(selectedAccount.getId());
			final Long bytesToProcess = new DirectorySizeCalculator(filesIOService, getMonitoringHelper()).apply(selectedFiles);
			getTaskProgressMap().put(taskId, tasksProgressService.create(TaskType.UPLOAD, taskId, bytesToProcess, selectedFiles));

			Predicate<UploadChunkContainer> filterEmptyUcc = ucc -> UploadChunkContainer.EMPTY != ucc;
			Consumer<UploadChunkContainer> updateProgress = ucc -> getTaskProgressMap().get(ucc.getTaskId())
				.process(ucc.getFileDto().getAbsolutePath(), ucc.getCurrentFileChunkCumulativeSize());
			Consumer<UploadChunkContainer> markFileAlreadyUploaded = ucc -> getTaskProgressMap().get(ucc.getTaskId())
				.markFileAlreadyUploaded(ucc.getFileDto().getAbsolutePath(), ucc.getFileDto().getSize());

			Consumer<UploadChunkContainer> persistTaskProgress = ucc -> {
				TaskProgress updated = tasksProgressService.update(getTaskProgressMap().get(ucc.getTaskId()));
				getTaskProgressMap().put(ucc.getTaskId(), updated);
			};

			Function<FileDto, UploadChunkContainer> packInContainer = fileDto -> new UploadChunkContainer(taskId, fileDto);
			Function<UploadChunkContainer, Stream<UploadChunkContainer>> parseDirectories = new DirectoryProcessor(filesIOService, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> generateFilehash = new UploadFileHasher(filesIOService, getMonitoringHelper());
			Predicate<UploadChunkContainer> removeFilesWithSize0 = ucc -> ucc.getFileDto().getSize() > 0;
			Function<UploadChunkContainer, UploadChunkContainer> storeFile = new FileStorer(fileServices, account, markFileAlreadyUploaded.andThen(persistTaskProgress));
			Function<UploadChunkContainer, Stream<UploadChunkContainer>> splitFileIntoChunks = new FileSplitter(account.getAttachmentSizeMB(), 2, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> generateChunkHash = new UploadChunkHasher(getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> chunkEncrypter = new ChunkEncrypter(cryptoService, account.getCryptoKey(), getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> saveOnIMAPServer = new ChunkSaver(connectionPoolService.getOrCreatePoolForAccount(account), cryptoService, account.getCryptoKey(), gitStatsUtil, getMonitoringHelper());
			Function<UploadChunkContainer, UploadChunkContainer> storeFileChunk = new FileChunkStorer(fileServices);

			selectedFiles.stream()
				.map(packInContainer)
				.flatMap(parseDirectories)
				.map(generateFilehash)
				.filter(removeFilesWithSize0)
				.map(storeFile)
				.filter(filterEmptyUcc)
				.flatMap(splitFileIntoChunks)
				.filter(filterEmptyUcc)
				.map(generateChunkHash)
				.map(ucc -> chunkEncryptionEnabled ? chunkEncrypter.apply(ucc) : ucc)
				.filter(filterEmptyUcc)
				.map(saveOnIMAPServer)
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
