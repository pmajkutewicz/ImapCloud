package pl.pamsoft.imapcloud.services.download;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.monitoring.MonHelper;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.common.ChunkHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.util.function.Function;

public class DownloadChunkHasher implements Function<DownloadChunkContainer, DownloadChunkContainer>, ChunkHasher {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadChunkHasher.class);

	private PerformanceDataService performanceDataService;

	public DownloadChunkHasher(PerformanceDataService performanceDataService) {
		this.performanceDataService = performanceDataService;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		LOG.debug("Validating chunk hash {} of {}", dcc.getChunkToDownload().getChunkNumber(), dcc.getChunkToDownload().getOwnerFile().getName());
		Monitor monitor = MonHelper.start(MonHelper.DL_CHUNK_HASHER);
		String hash = hash(dcc.getData());
		double lastVal = MonHelper.stop(monitor);
		performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_HASH, lastVal));
		LOG.debug("Hash validated in {}", lastVal);
		return DownloadChunkContainer.addChunkHash(dcc, hash);
	}
}
