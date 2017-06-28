package pl.pamsoft.imapcloud.services.delete;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkDeleter;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.DeleteChunkContainer;

import java.util.function.Function;

public class ChunkDeleterFacade implements Function<DeleteChunkContainer, DeleteChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkDeleterFacade.class);

	private ChunkDeleter chunkDeleter;
	private MonitoringHelper monitoringHelper;

	public ChunkDeleterFacade(ChunkDeleter chunkDeleter, MonitoringHelper monitoringHelper) {
		this.chunkDeleter = chunkDeleter;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public DeleteChunkContainer apply(DeleteChunkContainer dcc) {
		try {
			LOG.info("Deleting file {}", dcc.getFileUniqueId());
			Monitor monitor = monitoringHelper.start(Keys.DE_FILE_DELETER);

			boolean isDeleted = chunkDeleter.delete(dcc);

			double lastVal = monitoringHelper.stop(monitor);
			LOG.info("File deleted in {} ms", lastVal);
			return isDeleted ? DeleteChunkContainer.markAsDeleted(dcc) : DeleteChunkContainer.markAsNotDeleted(dcc);
		} catch (Exception e) {
			LOG.error("Error in stream", e);
		}

		LOG.warn("Returning EMPTY from ChunkDeleterFacade");
		return DeleteChunkContainer.EMPTY;
	}
}
