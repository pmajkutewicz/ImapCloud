package pl.pamsoft.imapcloud.services.download;

import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.nio.file.Path;
import java.nio.file.Paths;

class DestFileUtils {

	static Path generateDirPath(DownloadChunkContainer dcc) {
		return Paths.get(dcc.getDestinationDir().getAbsolutePath());
	}

	static Path generateFilePath(DownloadChunkContainer dcc) {
		return Paths.get(dcc.getDestinationDir().getAbsolutePath(), dcc.getChunkToDownload().getOwnerFile().getName());
	}
}
