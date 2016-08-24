package pl.pamsoft.imapcloud.services.download;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.common.ChunkHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.util.function.Function;

public class DownloadChunkHasher implements Function<DownloadChunkContainer, DownloadChunkContainer>, ChunkHasher {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadChunkHasher.class);

	private PerformanceDataService performanceDataService;
	private MonitoringHelper monitoringHelper;

	public DownloadChunkHasher(PerformanceDataService performanceDataService, MonitoringHelper monitoringHelper) {
		this.performanceDataService = performanceDataService;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		LOG.debug("Validating chunk hash {} of {}", dcc.getChunkToDownload().getChunkNumber(), dcc.getChunkToDownload().getOwnerFile().getName());
		Monitor monitor = monitoringHelper.start(Keys.DL_CHUNK_HASHER);
		String hash = hash(dcc.getData());
		double lastVal = monitoringHelper.stop(monitor);
		performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_HASH, lastVal));
		LOG.debug("Hash validated in {}", lastVal);
		return DownloadChunkContainer.addChunkHash(dcc, hash);
	}
}
