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
public class File {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private Long version;

	private String fileUniqueId;
	private String name;
	private String absolutePath;
	private Long size;
	private String fileHash;
	private boolean chunkEncryptionEnabled;
	private boolean completed;
	@ManyToOne
	@JoinColumn(name = "owner_account_id")
	private Account ownerAccount;

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

	public String getFileUniqueId() {
		return this.fileUniqueId;
	}

	public void setFileUniqueId(String fileUniqueId) {
		this.fileUniqueId = fileUniqueId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbsolutePath() {
		return this.absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getFileHash() {
		return this.fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public boolean isChunkEncryptionEnabled() {
		return chunkEncryptionEnabled;
	}

	public void setChunkEncryptionEnabled(boolean chunkEncryption) {
		this.chunkEncryptionEnabled = chunkEncryption;
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Account getOwnerAccount() {
		return this.ownerAccount;
	}

	public void setOwnerAccount(Account ownerAccount) {
		this.ownerAccount = ownerAccount;
	}
}
