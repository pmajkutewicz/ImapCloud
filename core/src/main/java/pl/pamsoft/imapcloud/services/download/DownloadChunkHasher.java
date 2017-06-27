package pl.pamsoft.imapcloud.services.download;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.common.ChunkHasher;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.util.function.Function;

public class DownloadChunkHasher implements Function<DownloadChunkContainer, DownloadChunkContainer>, ChunkHasher {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadChunkHasher.class);

	private MonitoringHelper monitoringHelper;

	public DownloadChunkHasher(MonitoringHelper monitoringHelper) {
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		LOG.debug("Validating chunk hash {} of {}", dcc.getChunkToDownload().getChunkNumber(), dcc.getChunkToDownload().getOwnerFile().getName());
		Monitor monitor = monitoringHelper.start(Keys.DL_CHUNK_HASHER);
		String hash = hash(dcc.getData());
		double lastVal = monitoringHelper.stop(monitor);
		LOG.debug("Hash validated in {}", lastVal);
		return DownloadChunkContainer.addChunkHash(dcc, hash);
	}
}
