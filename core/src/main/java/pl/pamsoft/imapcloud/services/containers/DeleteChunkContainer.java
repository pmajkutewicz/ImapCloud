package pl.pamsoft.imapcloud.services.containers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.concurrent.Immutable;

@Immutable
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public class DeleteChunkContainer implements pl.pamsoft.imapcloud.api.containers.DeleteChunkContainer {
	public static final DeleteChunkContainer EMPTY = new DeleteChunkContainer(null, null, null, null, null);

	private final String taskId;
	private final String fileUniqueId;
	private final String fileHash;
	private final Boolean isDeleted;
	private final Boolean isDeletedFromDb;

	public DeleteChunkContainer(String taskId, String fileUniqueId, String fileHash) {
		this(taskId, fileUniqueId, fileHash, null, null);
	}

	private DeleteChunkContainer(String taskId, String fileUniqueId, String fileHash, Boolean isDeleted, Boolean isDeletedFromDb) {
		this.taskId = taskId;
		this.fileUniqueId = fileUniqueId;
		this.fileHash = fileHash;
		this.isDeleted = isDeleted;
		this.isDeletedFromDb = isDeletedFromDb;
	}

	public static DeleteChunkContainer markAsDeleted(DeleteChunkContainer dcc) {
		return new DeleteChunkContainer(dcc.getTaskId(), dcc.getFileUniqueId(), dcc.getFileHash(), Boolean.TRUE, dcc.isDeletedFromDb);
	}

	public static DeleteChunkContainer markAsNotDeleted(DeleteChunkContainer dcc) {
		return new DeleteChunkContainer(dcc.getTaskId(), dcc.getFileUniqueId(), dcc.getFileHash(), Boolean.FALSE, dcc.isDeletedFromDb);
	}

	public static DeleteChunkContainer markAsDeletedFromDB(DeleteChunkContainer dcc) {
		return new DeleteChunkContainer(dcc.getTaskId(), dcc.getFileUniqueId(), dcc.getFileHash(), dcc.isDeleted, Boolean.TRUE);
	}

	public static DeleteChunkContainer markAsNotDeletedFromDB(DeleteChunkContainer dcc) {
		return new DeleteChunkContainer(dcc.getTaskId(), dcc.getFileUniqueId(), dcc.getFileHash(), dcc.isDeleted, Boolean.FALSE);
	}

	@Override
	public String toString() {
		return String.format("File: %s, isDeleted: %s, isDeletedInDb: %s", getFileUniqueId(), isDeleted, isDeletedFromDb);
	}

	@Override
	public String getTaskId() {
		return this.taskId;
	}

	@Override
	public String getFileUniqueId() {
		return fileUniqueId;
	}

	@Override
	public String getFileHash() {
		return fileHash;
	}

	@Override
	public Boolean getDeleted() {
		return isDeleted;
	}
}
