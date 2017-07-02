package pl.pamsoft.imapcloud.api.containers;

import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;

public interface DownloadChunkContainer {
	String getTaskId();

	FileChunk getChunkToDownload();

	FileDto getDestinationDir();

	byte[] getData();

	String getChunkHash();

	String getFileHash();
}
