package pl.pamsoft.imapcloud.api.containers;

import pl.pamsoft.imapcloud.dto.FileDto;

public interface UploadChunkContainer {
	String getFileChunkUniqueId();

	String getTaskId();

	FileDto getFileDto();

	String getFileHash();

	String getSavedFileId();

	String getFileUniqueId();

	long getChunkSize();

	long getCurrentFileChunkCumulativeSize();

	byte[] getData();

	boolean isEncrypted();

	int getChunkNumber();

	boolean isLastChunk();

	String getChunkHash();

	String getMessageId();
}
