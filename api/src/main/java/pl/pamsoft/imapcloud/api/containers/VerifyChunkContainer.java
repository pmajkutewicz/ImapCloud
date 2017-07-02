package pl.pamsoft.imapcloud.api.containers;

public interface VerifyChunkContainer {
	String getTaskId();

	String getFileChunkUniqueId();

	String getFileHash();

	String getStorageChunkId();

	Boolean getChunkExist();

	Boolean getChunkInfoUpdatedInDb();
}
