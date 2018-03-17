package pl.pamsoft.imapcloud.api.containers;

public interface UploadChunkContainer {
	String getFileChunkUniqueId();

	String getTaskId();

	String getFileHash();

	Long getSavedFileId();

	String getFileUniqueId();

	long getChunkSize();

	long getCurrentFileChunkCumulativeSize();

	byte[] getData();

	boolean isEncrypted();

	int getChunkNumber();

	boolean isLastChunk();

	String getChunkHash();

	String getStorageChunkId();

	long getUploadTimeMs();
}
