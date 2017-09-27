package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Version;
import java.util.HashMap;
import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN", "NM_SAME_SIMPLE_NAME_AS_INTERFACE"})
@Entity
public class Account implements pl.pamsoft.imapcloud.api.accounts.Account {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private Long version;

	private String login;
	private String type;
	private String host;
	private String password;
	private Integer maxConcurrentConnections;
	private Integer accountSizeMB;
	private Integer attachmentSizeMB;
	private String cryptoKey;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name="name")
	@Column(name="value")
	@CollectionTable(name="additional_properties", joinColumns=@JoinColumn(name="account_id"))
	private Map<String, String> additionalProperties = new HashMap<>();

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Integer getMaxConcurrentConnections() {
		return this.maxConcurrentConnections;
	}

	public void setMaxConcurrentConnections(Integer maxConcurrentConnections) {
		this.maxConcurrentConnections = maxConcurrentConnections;
	}

	@Override
	public Integer getAccountSizeMB() {
		return accountSizeMB;
	}

	public void setAccountSizeMB(Integer accountSizeMB) {
		this.accountSizeMB = accountSizeMB;
	}

	@Override
	public Integer getAttachmentSizeMB() {
		return this.attachmentSizeMB;
	}

	public void setAttachmentSizeMB(Integer attachmentSizeMB) {
		this.attachmentSizeMB = attachmentSizeMB;
	}

	@Override
	public String getCryptoKey() {
		return this.cryptoKey;
	}

	public void setCryptoKey(String cryptoKey) {
		this.cryptoKey = cryptoKey;
	}

	@Override
	public Map<String, String> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, String> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public String getProperty(String name) {
		return additionalProperties.get(name);
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
