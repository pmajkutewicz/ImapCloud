package pl.pamsoft.imapcloud.api.containers;

public interface VerifyChunkContainer {
	String getTaskId();

	String getFileChunkUniqueId();

	String getFileHash();

	String getStoredChunkId();

	Boolean getChunkExist();

	Boolean getChunkInfoUpdatedInDb();
}
