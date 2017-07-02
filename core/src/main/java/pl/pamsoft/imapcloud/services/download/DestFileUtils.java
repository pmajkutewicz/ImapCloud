package pl.pamsoft.imapcloud.services.download;

import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.nio.file.Path;
import java.nio.file.Paths;

class DestFileUtils {

	protected static Path generateDirPath(DownloadChunkContainer dcc) {
		return Paths.get(dcc.getDestinationDir().getAbsolutePath());
	}

	protected static Path generateFilePath(DownloadChunkContainer dcc) {
		return Paths.get(dcc.getDestinationDir().getAbsolutePath(), dcc.getChunkToDownload().getOwnerFile().getName());
	}
}
