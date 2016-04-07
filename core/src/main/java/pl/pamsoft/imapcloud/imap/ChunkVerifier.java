package pl.pamsoft.imapcloud.imap;

import com.google.common.base.Stopwatch;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;
import java.util.function.Function;

public class ChunkVerifier implements Function<FileChunk, Boolean> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkVerifier.class);

	private final GenericObjectPool<Store> connectionPool;
	private CryptoService cryptoService;
	private FileChunkRepository fileChunkRepository;
	private final Statistics statistics;
	private final PerformanceDataService performanceDataService;

	public ChunkVerifier(GenericObjectPool<Store> connectionPool, CryptoService cryptoService, FileChunkRepository fileChunkRepository, Statistics statistics, PerformanceDataService performanceDataService) {
		this.connectionPool = connectionPool;
		this.cryptoService = cryptoService;
		this.fileChunkRepository = fileChunkRepository;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	public Boolean apply(FileChunk fileChunk) {
		Store store = null;
		try {
			LOG.info("Verifying chunk {}", fileChunk.getFileChunkUniqueId());
			Stopwatch stopwatch = Stopwatch.createStarted();
			store = connectionPool.borrowObject();
			String folderName = IMAPUtils.createFolderName(cryptoService, fileChunk.getOwnerFile().getAbsolutePath());
			Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
			folder.open(Folder.READ_ONLY);
			SearchTerm messageIDTerm = new MessageIDTerm(fileChunk.getMessageId());
			Message[] search = folder.search(messageIDTerm);
			boolean chunkExists = search.length == 1;
			fileChunkRepository.markChunkVerified(fileChunk.getId(), chunkExists);
			folder.close(IMAPUtils.NO_EXPUNGE);
			statistics.add(StatisticType.CHUNK_VERIFIER, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_VERIFIER, stopwatch));
			LOG.debug("Chunk verified in {}", stopwatch);
			return chunkExists;
		} catch (Exception e) {
			LOG.error("Error in stream", e);
			try {
				connectionPool.invalidateObject(store);
			} catch (Exception e1) {
				LOG.error("Error invalidating", e1);
			}
		} finally {
			if (null != store) {
				connectionPool.returnObject(store);
			}
		}
		return Boolean.FALSE;
	}
}