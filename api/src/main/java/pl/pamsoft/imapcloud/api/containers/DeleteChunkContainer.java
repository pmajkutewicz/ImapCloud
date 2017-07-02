package pl.pamsoft.imapcloud.api.containers;

public interface DeleteChunkContainer {
	String getTaskId();

	String getFileUniqueId();

	String getFileHash();

	Boolean getDeleted();
}
