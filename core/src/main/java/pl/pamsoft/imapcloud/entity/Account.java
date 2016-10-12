package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Id;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class Account {
	@Id
	private String id;
	private String login;
	private String email;
	private String imapServerAddress;
	private String password;
	private Integer maxConcurrentConnections;
	private Integer sizeMB;
	private Integer attachmentSizeMB;
	private String cryptoKey;

	public Account() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImapServerAddress() {
		return this.imapServerAddress;
	}

	public void setImapServerAddress(String imapServerAddress) {
		this.imapServerAddress = imapServerAddress;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMaxConcurrentConnections() {
		return this.maxConcurrentConnections;
	}

	public void setMaxConcurrentConnections(Integer maxConcurrentConnections) {
		this.maxConcurrentConnections = maxConcurrentConnections;
	}

	public Integer getSizeMB() {
		return this.sizeMB;
	}

	public void setSizeMB(Integer sizeMB) {
		this.sizeMB = sizeMB;
	}

	public Integer getAttachmentSizeMB() {
		return this.attachmentSizeMB;
	}

	public void setAttachmentSizeMB(Integer attachmentSizeMB) {
		this.attachmentSizeMB = attachmentSizeMB;
	}

	public String getCryptoKey() {
		return this.cryptoKey;
	}

	public void setCryptoKey(String cryptoKey) {
		this.cryptoKey = cryptoKey;
	}
}
