package pl.pamsoft.imapcloud.services.download;

import com.jamonapi.Monitor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.common.FileHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.IOException;
import java.util.function.Function;

public class DownloadFileHasher implements Function<DownloadChunkContainer, DownloadChunkContainer>, FileHasher {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadFileHasher.class);

	private FilesIOService filesIOService;
	private PerformanceDataService performanceDataService;
	private MonitoringHelper monitoringHelper;

	public DownloadFileHasher(FilesIOService filesIOService, PerformanceDataService performanceDataService, MonitoringHelper monitoringHelper) {
		this.filesIOService = filesIOService;
		this.performanceDataService = performanceDataService;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		if (dcc.getChunkToDownload().isLastChunk()) {
			LOG.debug("Hashing file {}", dcc.getChunkToDownload().getOwnerFile().getName());
			Monitor monitor = monitoringHelper.start(Keys.DL_FILE_HASHER);
			try {
				String hash = hash(DestFileUtils.generateFilePath(dcc).toFile());
				double lastVal = monitoringHelper.stop(monitor);
				performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.FILE_HASH, lastVal));
				LOG.debug("File hash generated in {}", lastVal);
				return DownloadChunkContainer.addFileHash(dcc, hash);
			} catch (IOException ex) {
				LOG.error(String.format("Can't calculate hash for file: %s", dcc.getChunkToDownload().getOwnerFile().getName()), ex);
			}
			LOG.warn("Returning EMPTY from DownloadFileHasher");
		}
		return dcc;
	}

	@Override
	public FilesIOService getFilesIOService() {
		return filesIOService;
	}
}
