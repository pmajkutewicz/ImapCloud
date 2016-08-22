package pl.pamsoft.imapcloud.services.upload;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.monitoring.MonHelper;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.common.ChunkHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.util.function.Function;

public class UploadChunkHasher implements Function<UploadChunkContainer, UploadChunkContainer>, ChunkHasher {

	private static final Logger LOG = LoggerFactory.getLogger(UploadChunkHasher.class);

	private PerformanceDataService performanceDataService;

	public UploadChunkHasher(PerformanceDataService performanceDataService) {
		this.performanceDataService = performanceDataService;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		LOG.debug("Hashing chunk {} of {}", chunk.getChunkNumber(), chunk.getFileDto().getName());
		Monitor monitor = MonHelper.start(MonHelper.UL_CHUNK_HASHER);
		String hash = hash(chunk.getData());
		double lastVal = MonHelper.stop(monitor);
		performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_HASH, lastVal));
		LOG.debug("Hash generated in {}", lastVal);
		return UploadChunkContainer.addChunkHash(chunk, hash);
	}

}
