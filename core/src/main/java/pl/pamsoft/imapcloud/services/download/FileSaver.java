package pl.pamsoft.imapcloud.services.download;

import org.apache.commons.io.FileUtils;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

public class FileSaver implements Function<DownloadChunkContainer, DownloadChunkContainer> {
	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		try {
			Path path = DestFileUtils.generateDirPath(dcc);
			Path pathWithFile = DestFileUtils.generateFilePath(dcc);
			createIfNecessary(path, pathWithFile);
			Files.write(pathWithFile, dcc.getData(), StandardOpenOption.APPEND);
		} catch (IOException e) {
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
