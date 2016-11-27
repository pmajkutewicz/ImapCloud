package pl.pamsoft.imapcloud.services.upload;

import com.jamonapi.Monitor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.common.FileHasher;

import java.io.IOException;
import java.util.function.Function;

public class UploadFileHasher implements Function<UploadChunkContainer, UploadChunkContainer>, FileHasher {

	private static final Logger LOG = LoggerFactory.getLogger(UploadFileHasher.class);

	private FilesIOService filesIOService;
	private MonitoringHelper monitoringHelper;

	public UploadFileHasher(FilesIOService filesIOService, MonitoringHelper monitoringHelper) {
		this.filesIOService = filesIOService;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		LOG.debug("Hashing file {}", chunk.getFileDto().getName());
		Monitor monitor = monitoringHelper.start(Keys.UL_FILE_HASHER);
		try {
			String hash = hash(filesIOService.getFile(chunk.getFileDto()));
			double lastVal = monitoringHelper.stop(monitor);
			LOG.debug("File hash generated in {}", lastVal);
			return UploadChunkContainer.addFileHash(chunk, hash);
		} catch (IOException ex) {
			LOG.error(String.format("Can't calculate hash for file: %s", chunk.getFileDto().getAbsolutePath()), ex);
		}
		LOG.warn("Returning EMPTY from UploadFileHasher");
		return UploadChunkContainer.EMPTY;
	}

	@Override
	public FilesIOService getFilesIOService() {
		return filesIOService;
	}
}
