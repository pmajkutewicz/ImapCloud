package pl.pamsoft.imapcloud.services.containers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.concurrent.Immutable;

@Immutable
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public class VerifyChunkContainer implements pl.pamsoft.imapcloud.api.containers.VerifyChunkContainer {
	public static final VerifyChunkContainer EMPTY = new VerifyChunkContainer(null, null, null, null, null, null, null);

	private final String taskId;
	private final String fileChunkUniqueId;
	private final String fileHash;
	private final Long dbChunkId;
	private final String storageChunkId;
	private final Boolean chunkExist;
	private final Boolean chunkInfoUpdatedInDb;

	public VerifyChunkContainer(String taskId, String fileChunkUniqueId, String fileHash, Long dbChunkId, String storageChunkId) {
		this(taskId, fileChunkUniqueId, fileHash, dbChunkId, storageChunkId, null, null);
	}

	private VerifyChunkContainer(String taskId, String fileChunkUniqueId, String fileHash, Long dbChunkId, String storageChunkId, Boolean chunkExist, Boolean chunkInfoUpdatedInDb) {
		this.taskId = taskId;
		this.fileChunkUniqueId = fileChunkUniqueId;
		this.fileHash = fileHash;
		this.dbChunkId = dbChunkId;
		this.storageChunkId = storageChunkId;
		this.chunkExist = chunkExist;
		this.chunkInfoUpdatedInDb = chunkInfoUpdatedInDb;
	}

	@SuppressFBWarnings("OCP_OVERLY_CONCRETE_PARAMETER")
	public static VerifyChunkContainer markAsExist(VerifyChunkContainer vcc) {
		return new VerifyChunkContainer(vcc.getTaskId(), vcc.getFileChunkUniqueId(), vcc.getFileHash(), vcc.getDbChunkId(), vcc.getStorageChunkId(), Boolean.TRUE, vcc.getChunkInfoUpdatedInDb());
	}

	@SuppressFBWarnings("OCP_OVERLY_CONCRETE_PARAMETER")
	public static VerifyChunkContainer markAsNotExist(VerifyChunkContainer vcc) {
		return new VerifyChunkContainer(vcc.getTaskId(), vcc.getFileChunkUniqueId(), vcc.getFileHash(), vcc.getDbChunkId(), vcc.getStorageChunkId(), Boolean.FALSE, vcc.getChunkInfoUpdatedInDb());
	}

	@SuppressFBWarnings("OCP_OVERLY_CONCRETE_PARAMETER")
	public static VerifyChunkContainer markAsUpdatedInDb(VerifyChunkContainer vcc) {
		return new VerifyChunkContainer(vcc.getTaskId(), vcc.getFileChunkUniqueId(), vcc.getFileHash(), vcc.getDbChunkId(), vcc.getStorageChunkId(), vcc.getChunkExist(), Boolean.TRUE);
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	/**
	 * Unique chunk id in db.
	 */
	@Override
	public String getFileChunkUniqueId() {
		return fileChunkUniqueId;
	}

	@Override
	public String getFileHash() {
		return fileHash;
	}

	/**
	 * Unique chunk id returned by underlying storage engine (imap, ftp etc.).
	 */
	@Override
	public String getStorageChunkId() {
		return storageChunkId;
	}

	@Override
	public Boolean getChunkExist() {
		return chunkExist;
	}

	@Override
	public Boolean getChunkInfoUpdatedInDb() {
		return chunkInfoUpdatedInDb;
	}

	public Long getDbChunkId() {
		return dbChunkId;
	}
}
