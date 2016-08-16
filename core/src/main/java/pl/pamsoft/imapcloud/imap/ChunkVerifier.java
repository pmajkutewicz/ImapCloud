package pl.pamsoft.imapcloud.imap;

import com.jamonapi.Monitor;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonHelper;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import java.util.function.Function;

public class ChunkVerifier implements Function<FileChunk, Boolean> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkVerifier.class);

	private final GenericObjectPool<Store> connectionPool;
	private final PerformanceDataService performanceDataService;

	public ChunkVerifier(GenericObjectPool<Store> connectionPool, PerformanceDataService performanceDataService) {
		this.connectionPool = connectionPool;
		this.performanceDataService = performanceDataService;
	}

	@Override
	public Boolean apply(FileChunk fileChunk) {
		Store store = null;
		try {
			LOG.info("Verifying chunk {}", fileChunk.getFileChunkUniqueId());
			Monitor monitor = MonHelper.get(this);
			store = connectionPool.borrowObject();
			String folderName = IMAPUtils.createFolderName(fileChunk);
			Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
			folder.open(Folder.READ_ONLY);

			Message[] search = folder.search(new MessageIdSearchTerm(fileChunk.getMessageId()));
			boolean chunkExists = search.length == 1;

			folder.close(IMAPUtils.NO_EXPUNGE);
			double lastVal = MonHelper.stop(monitor);
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_VERIFIER, lastVal));
			LOG.debug("Chunk verified in {}", lastVal);
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
