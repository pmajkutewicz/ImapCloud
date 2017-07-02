package pl.pamsoft.imapcloud.api.containers;

public interface DownloadChunkContainer {
	String getTaskId();

	String getExpectedChunkHash();

	String getExpectedFileHash();

	byte[] getData();

	String getStorageChunkId();

	String getChunkHash();

	String getFileHash();
}
