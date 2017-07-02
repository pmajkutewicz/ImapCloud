package pl.pamsoft.imapcloud.api.containers;

public interface UploadChunkContainer {
	String getFileChunkUniqueId();

	String getTaskId();

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

	String getStorageChunkId();
}
