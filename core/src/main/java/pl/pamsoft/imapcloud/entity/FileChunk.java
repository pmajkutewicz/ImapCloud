package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
@Entity
public class FileChunk {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private Long version;

	private String fileChunkUniqueId;
	private int chunkNumber;
	private String chunkHash;
	private Long orgSize;
	private Long encryptedSize;
	@ManyToOne
	@JoinColumn(name="owner_file_id")
	private File ownerFile;
	private String messageId; //FIXME: rename to getStoredChunkId ?
	private Long uploadTimeMs;
	private boolean lastChunk;
	private Long lastVerifiedAt;
	private Boolean chunkExists;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
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

	public Long getOrgSize() {
		return orgSize;
	}

	public void setOrgSize(Long orgSize) {
		this.orgSize = orgSize;
	}

	public Long getEncryptedSize() {
		return encryptedSize;
	}

	public void setEncryptedSize(Long encryptedSize) {
		this.encryptedSize = encryptedSize;
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

	public Long getUploadTimeMs() {
		return uploadTimeMs;
	}

	public void setUploadTimeMs(Long uploadTimeMs) {
		this.uploadTimeMs = uploadTimeMs;
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
