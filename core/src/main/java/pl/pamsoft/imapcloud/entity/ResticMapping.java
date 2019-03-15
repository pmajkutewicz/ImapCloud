package pl.pamsoft.imapcloud.entity;

import pl.pamsoft.imapcloud.restic.ResticType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class ResticMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Version
	private Long version;

	@ManyToOne
	@JoinColumn(name = "owner_account_id")
	private Account ownerAccount;

	@Enumerated(EnumType.STRING)
	private ResticType type;
	private String resticId;

	@ManyToOne
	@JoinColumn(name = "file_id")
	private File file;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Account getOwnerAccount() {
		return ownerAccount;
	}

	public void setOwnerAccount(Account ownerAccount) {
		this.ownerAccount = ownerAccount;
	}

	public ResticType getType() {
		return type;
	}

	public void setType(ResticType type) {
		this.type = type;
	}

	public String getResticId() {
		return resticId;
	}

	public void setResticId(String resticId) {
		this.resticId = resticId;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
