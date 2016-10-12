package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Id;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileChunk {
	@Id
	private String id;
	private Integer version;

	private String fileChunkUniqueId;
	private int chunkNumber;
	private String chunkHash;
	private Long size;
	private File ownerFile;
	private String messageId;
	private boolean lastChunk;
	private Long lastVerifiedAt;
	private Boolean chunkExists;

	public FileChunk() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getFileChunkUniqueId() {
		return this.fileChunkUniqueId;
	}

	public void setFileChunkUniqueId(String fileChunkUniqueId) {
		this.fileChunkUniqueId = fileChunkUniqueId;
	}

	public int getChunkNumber() {
		return this.chunkNumber;
	}

	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public String getChunkHash() {
		return this.chunkHash;
	}

	public void setChunkHash(String chunkHash) {
		this.chunkHash = chunkHash;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public File getOwnerFile() {
		return this.ownerFile;
	}

	public void setOwnerFile(File ownerFile) {
		this.ownerFile = ownerFile;
	}

	public String getMessageId() {
		return this.messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public boolean isLastChunk() {
		return this.lastChunk;
	}

	public void setLastChunk(boolean lastChunk) {
		this.lastChunk = lastChunk;
	}

	public Long getLastVerifiedAt() {
		return this.lastVerifiedAt;
	}

	public void setLastVerifiedAt(Long lastVerifiedAt) {
		this.lastVerifiedAt = lastVerifiedAt;
	}

	public Boolean getChunkExists() {
		return this.chunkExists;
	}

	public void setChunkExists(Boolean chunkExists) {
		this.chunkExists = chunkExists;
	}
}
