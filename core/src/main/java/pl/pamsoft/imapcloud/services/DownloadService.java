package pl.pamsoft.imapcloud.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.download.ChunkDecrypter;
import pl.pamsoft.imapcloud.services.download.ChunkDownloadFacade;
import pl.pamsoft.imapcloud.services.download.ChunkHashVerifier;
import pl.pamsoft.imapcloud.services.download.DownloadChunkHasher;
import pl.pamsoft.imapcloud.services.download.DownloadFileHasher;
import pl.pamsoft.imapcloud.services.download.FileHashVerifier;
import pl.pamsoft.imapcloud.services.download.FileSaver;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class DownloadService extends AbstractBackgroundService {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadService.class);

	@Autowired
	private AccountServicesHolder accountServicesHolder;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private CryptoService cryptoService;

	@SuppressFBWarnings("STT_TOSTRING_STORED_IN_FIELD")
	public boolean download(UploadedFileDto fileToDownload, FileDto destDir) throws RejectedExecutionException {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> task = runAsyncOnExecutor(() -> {
			try {
				Thread.currentThread().setName("DT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
				download(fileToDownload, destDir, taskId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		getTaskMap().put(taskId, task);
		return true;
	}

	public void downloadSync(UploadedFileDto fileToDownload, FileDto destDir) throws RejectedExecutionException {
		final String taskId = UUID.randomUUID().toString();
		download(fileToDownload, destDir, taskId);
	}

	private void download(UploadedFileDto fileToDownload, FileDto destDir, String taskId) throws RejectedExecutionException {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		File file = fileRepository.getByFileUniqueId(fileToDownload.getFileUniqueId());
		Account account = file.getOwnerAccount();
		AccountService accountService = accountServicesHolder.getAccountService(account.getType());
		List<FileChunk> chunkToDownload = fileChunkRepository.getFileChunks(fileToDownload.getFileUniqueId());

		Function<FileChunk, DownloadChunkContainer> packInContainer = fileChunk -> new DownloadChunkContainer(taskId, fileChunk, destDir, fileChunk.getChunkHash(), fileChunk.getOwnerFile().getFileHash());
		Predicate<DownloadChunkContainer> filterOutInvalidFiles = dcc -> !invalidFileIds.contains(dcc.getChunkToDownload().getOwnerFile().getFileUniqueId());
		Function<DownloadChunkContainer, DownloadChunkContainer> chunkDownloader = new ChunkDownloadFacade(accountService.getChunkDownloader(account), getMonitoringHelper());
		Function<DownloadChunkContainer, DownloadChunkContainer> chunkDecoder = new ChunkDecrypter(cryptoService, account.getCryptoKey(), getMonitoringHelper());
		Function<DownloadChunkContainer, DownloadChunkContainer> downloadChunkHasher = new DownloadChunkHasher(getMonitoringHelper());
		Function<DownloadChunkContainer, DownloadChunkContainer> chunkHashVerifier = new ChunkHashVerifier(invalidFileIds);
		Function<DownloadChunkContainer, DownloadChunkContainer> fileSaver = new FileSaver(getMonitoringHelper());
		Function<DownloadChunkContainer, DownloadChunkContainer> downloadFileHasher = new DownloadFileHasher(filesIOService, getMonitoringHelper());
		Function<DownloadChunkContainer, DownloadChunkContainer> fileHashVerifier = new FileHashVerifier(invalidFileIds);

		chunkToDownload.stream()
			.peek(c -> LOG.info("Processing {}", c.getChunkNumber()))
			.map(packInContainer)
			.filter(filterOutInvalidFiles)
			.map(chunkDownloader)
			.map(chunkDecoder)
			.map(downloadChunkHasher)
			.map(chunkHashVerifier)
			.map(fileSaver)
			.map(downloadFileHasher)
			.map(fileHashVerifier)
			.forEach(c -> LOG.info("Done: {}", c.getChunkToDownload().getChunkNumber()));
	}

	protected int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	protected String getNameFormat() {
		return "DownloadTask-%d";
	}

}
