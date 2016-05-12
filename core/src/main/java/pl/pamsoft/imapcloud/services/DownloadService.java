package pl.pamsoft.imapcloud.services;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.imap.ChunkLoader;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.download.ChunkDecoder;
import pl.pamsoft.imapcloud.services.download.ChunkHashVerifier;
import pl.pamsoft.imapcloud.services.download.DownloadChunkHasher;
import pl.pamsoft.imapcloud.services.download.DownloadFileHasher;
import pl.pamsoft.imapcloud.services.download.FileSaver;
import pl.pamsoft.imapcloud.services.download.FileHashVerifier;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class DownloadService extends AbstractBackgroundService {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadService.class);

	@Autowired
	private FileChunkRepository fileChunkRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private Statistics statistics;

	@Autowired
	private PerformanceDataService performanceDataService;

	@SuppressFBWarnings("STT_TOSTRING_STORED_IN_FIELD")
	public boolean download(UploadedFileDto fileToDownload, FileDto destDir) throws RejectedExecutionException {
		final String taskId = UUID.randomUUID().toString();
		Future<?> task = getExecutor().submit(() -> {
			try {
				Thread.currentThread().setName("DownloadTask-" + taskId);
				final MessageDigest instance = MessageDigest.getInstance("SHA-512");
				ConcurrentHashMap<String, String> invalidFileIds = new ConcurrentHashMap<>();
				File file = fileRepository.getByFileUniqueId(fileToDownload.getFileUniqueId());
				Account account = file.getOwnerAccount();
				List<FileChunk> chunkToDownload = fileChunkRepository.getFileChunks(fileToDownload.getFileUniqueId());
				GenericObjectPool<Store> connectionPool = connectionPoolService.getOrCreatePoolForAccount(account);

				Function<FileChunk, DownloadChunkContainer> packInContainer = fileChunk -> new DownloadChunkContainer(taskId, fileChunk, destDir);
				Predicate<DownloadChunkContainer> filterOutInvalidFiles = dcc -> !invalidFileIds.containsKey(dcc.getChunkToDownload().getOwnerFile().getFileUniqueId());
				Function<DownloadChunkContainer, DownloadChunkContainer> chunkLoader = new ChunkLoader(connectionPool, statistics, performanceDataService);
				Function<DownloadChunkContainer, DownloadChunkContainer> chunkDecoder = new ChunkDecoder(cryptoService, account.getCryptoKey(), statistics, performanceDataService);
				Function<DownloadChunkContainer, DownloadChunkContainer> downloadChunkHasher = new DownloadChunkHasher(instance, statistics, performanceDataService);
				Function<DownloadChunkContainer, DownloadChunkContainer> chunkHashVerifier = new ChunkHashVerifier(invalidFileIds);
				Function<DownloadChunkContainer, DownloadChunkContainer> fileSaver = new FileSaver();
				Function<DownloadChunkContainer, DownloadChunkContainer> downloadFileHasher = new DownloadFileHasher(instance, statistics, performanceDataService);
				Function<DownloadChunkContainer, DownloadChunkContainer> fileHashVerifier = new FileHashVerifier(invalidFileIds);

				chunkToDownload.stream()
					.peek(c -> LOG.info("Processing {}", c.getChunkNumber()))
					.map(packInContainer)
					.filter(filterOutInvalidFiles)
					.map(chunkLoader)
					.map(chunkDecoder)
					.map(downloadChunkHasher)
					.map(chunkHashVerifier)
					.map(fileSaver)
					.map(downloadFileHasher)
					.map(fileHashVerifier)
					.forEach(c -> LOG.info("Done: {}", c.getChunkToDownload().getChunkNumber()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		getTaskMap().put(taskId, task);
		return true;
	}

	int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	String getNameFormat() {
		return "DownloadTask-%d";
	}
}
