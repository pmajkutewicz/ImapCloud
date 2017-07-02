package pl.pamsoft.imapcloud.api.containers;

public interface DownloadChunkContainer {
	String getTaskId();

	byte[] getData();

	String getMessageId();

	String getChunkHash();

	String getFileHash();
}
