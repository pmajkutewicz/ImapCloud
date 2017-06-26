package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Id;
import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class Account {
	@Id
	private String id;
	private String login;
	private String type;
	private String host;
	private String password;
	private Integer maxConcurrentConnections;
	private Integer accountSizeMB;
	private Integer attachmentSizeMB;
	private String cryptoKey;
	private Map<String, String> additionalProperties;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public Integer getAccountSizeMB() {
		return accountSizeMB;
	}

	public void setAccountSizeMB(Integer accountSizeMB) {
		this.accountSizeMB = accountSizeMB;
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

	public Map<String, String> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, String> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public String getProperty(String name) {
		return additionalProperties.get(name);
	}
}
