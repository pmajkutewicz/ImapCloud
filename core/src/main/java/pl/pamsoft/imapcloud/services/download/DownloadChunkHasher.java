package pl.pamsoft.imapcloud.services.download;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.common.ChunkHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.security.MessageDigest;
import java.util.function.Function;

public class DownloadChunkHasher implements Function<DownloadChunkContainer, DownloadChunkContainer>, ChunkHasher {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadChunkHasher.class);

	private MessageDigest md;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public DownloadChunkHasher(MessageDigest messageDigest, Statistics statistics, PerformanceDataService performanceDataService) {
		this.md = messageDigest;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		LOG.debug("Validating chunk hash {} of {}", dcc.getChunkToDownload().getChunkNumber(), dcc.getChunkToDownload().getOwnerFile().getName());
		Stopwatch stopwatch = Stopwatch.createStarted();
		String hash = hash(dcc.getData());
		statistics.add(StatisticType.CHUNK_HASH, stopwatch.stop());
		performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_HASH, stopwatch));
		LOG.debug("Hash validated in {}", stopwatch);
		return DownloadChunkContainer.addChunkHash(dcc, hash);
	}

	@Override
	public MessageDigest getMessageDigest() {
		return md;
	}
}
