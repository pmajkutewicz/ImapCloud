package pl.pamsoft.imapcloud.services.upload;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.common.ChunkHasher;

import java.util.function.Function;

public class UploadChunkHasher implements Function<UploadChunkContainer, UploadChunkContainer>, ChunkHasher {

	private static final Logger LOG = LoggerFactory.getLogger(UploadChunkHasher.class);
	private MonitoringHelper monitoringHelper;

	public UploadChunkHasher(MonitoringHelper monitoringHelper) {
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		LOG.debug("Hashing chunk {} of {}", chunk.getChunkNumber(), chunk.getFileDto().getName());
		Monitor monitor = monitoringHelper.start(Keys.UL_CHUNK_HASHER);
		String hash = hash(chunk.getData());
		double lastVal = monitoringHelper.stop(monitor);
		LOG.debug("Hash generated in {}", lastVal);
		return UploadChunkContainer.addChunkHash(chunk, hash);
	}

}
