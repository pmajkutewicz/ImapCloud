package pl.pamsoft.imapcloud.services.download;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkDownloader;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.util.function.Function;

public class ChunkDownloadFacade implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkDownloadFacade.class);
	private ChunkDownloader downloader;
	private MonitoringHelper monitoringHelper;

	public ChunkDownloadFacade(ChunkDownloader downloader, MonitoringHelper monitoringHelper) {
		this.downloader = downloader;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		try {
			FileChunk fileChunk = dcc.getChunkToDownload();
			LOG.info("Downloading chunk {} of {}", fileChunk.getChunkNumber(), fileChunk.getOwnerFile().getName());
			Monitor monitor = monitoringHelper.start(Keys.DL_CHUNK_LOADER);

			byte[] downloaded = downloader.download(dcc);

			double lastVal = monitoringHelper.stop(monitor);
			LOG.info("Chunk downloaded in {} ms", lastVal);
			return DownloadChunkContainer.addData(dcc, downloaded);
		} catch (Exception e) {
			LOG.error("Error in stream", e);
		}

		LOG.warn("Returning EMPTY from ChunkDownloadFacade");
		return DownloadChunkContainer.EMPTY;
	}

}
