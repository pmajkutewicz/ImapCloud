package pl.pamsoft.imapcloud.services.verify;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkVerifier;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.VerifyChunkContainer;

import java.util.function.Function;

public class ChunkVerifierFacade implements Function<VerifyChunkContainer, VerifyChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkVerifierFacade.class);

	private ChunkVerifier chunkVerifier;
	private MonitoringHelper monitoringHelper;

	public ChunkVerifierFacade(ChunkVerifier chunkVerifier, MonitoringHelper monitoringHelper) {
		this.chunkVerifier = chunkVerifier;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public VerifyChunkContainer apply(VerifyChunkContainer vcc) {
		try {
			LOG.info("Verifying chunk {}", vcc.getFileChunkUniqueId());
			Monitor monitor = monitoringHelper.start(Keys.VR_CHUNK_VERIFIER);

			boolean isExist = chunkVerifier.verify(vcc);

			double lastVal = monitoringHelper.stop(monitor);
			LOG.info("Chunk verified in {} ms", lastVal);
			return isExist ? VerifyChunkContainer.markAsExist(vcc) : VerifyChunkContainer.markAsNotExist(vcc);
		} catch (Exception e) {
			LOG.error("Error in stream", e);
		}

		LOG.warn("Returning EMPTY from ChunkVerifierFacade");
		return VerifyChunkContainer.EMPTY;
	}
}
