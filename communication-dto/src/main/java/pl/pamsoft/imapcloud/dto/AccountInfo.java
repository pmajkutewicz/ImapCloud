package pl.pamsoft.imapcloud.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class AccountInfo {
	private String domain;
	private String host;
	private LoginType loginType;
	private Integer maxConcurrentConnections;
	private Integer sizeMB;
	private Integer attachmentSizeMB;

	public AccountInfo(String domain, String host, LoginType loginType, Integer maxConcurrentConnections, Integer sizeMB, Integer attachmentSizeMB) {
		this.domain = domain;
		this.host = host;
		this.loginType = loginType;
		this.maxConcurrentConnections = maxConcurrentConnections;
		this.sizeMB = sizeMB;
		this.attachmentSizeMB = attachmentSizeMB;
	}

	public AccountInfo() {
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public LoginType getLoginType() {
		return this.loginType;
	}

	public void setLoginType(LoginType loginType) {
		this.loginType = loginType;
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
}




