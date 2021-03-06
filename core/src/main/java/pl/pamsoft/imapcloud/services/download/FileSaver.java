package pl.pamsoft.imapcloud.services.download;

import com.jamonapi.Monitor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

public class FileSaver implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileSaver.class);

	private MonitoringHelper monitoringHelper;

	public FileSaver(MonitoringHelper monitoringHelper) {
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		try {
			LOG.debug("Appending chunk {} in {}", dcc.getChunkToDownload().getChunkNumber(), dcc.getDestinationDir().getName());
			Monitor monitor = monitoringHelper.start(Keys.DL_CHINK_APPENDER);
			Path path = DestFileUtils.generateDirPath(dcc);
			Path pathWithFile = DestFileUtils.generateFilePath(dcc);
			createIfNecessary(path, pathWithFile);
			Files.write(pathWithFile, dcc.getData(), StandardOpenOption.APPEND);
			double lastVal = monitoringHelper.stop(monitor);
			LOG.debug("Chunk appended in {} ms", lastVal);
		} catch (IOException e) {
			//FIXME: can't append chunk to file... what to do?
			e.printStackTrace();
		}
		return dcc;
	}

	private void createIfNecessary(Path dirPath, Path filePath) throws IOException {
		FileUtils.forceMkdir(dirPath.toFile());
		if (Files.notExists(filePath)) {
			Files.createFile(filePath);
		}
	}
}
