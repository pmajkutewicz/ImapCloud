package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.common.ChunkHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.util.function.Function;

public class UploadChunkHasher implements Function<UploadChunkContainer, UploadChunkContainer>, ChunkHasher {

	private static final Logger LOG = LoggerFactory.getLogger(UploadChunkHasher.class);

	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public UploadChunkHasher(Statistics statistics, PerformanceDataService performanceDataService) {
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		LOG.debug("Hashing chunk {} of {}", chunk.getChunkNumber(), chunk.getFileDto().getName());
		Stopwatch stopwatch = Stopwatch.createStarted();
		String hash = hash(chunk.getData());
		statistics.add(StatisticType.CHUNK_HASH, stopwatch.stop());
		performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_HASH, stopwatch));
		LOG.debug("Hash generated in {}", stopwatch);
		return UploadChunkContainer.addChunkHash(chunk, hash);
	}

}
