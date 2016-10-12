package pl.pamsoft.imapcloud.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class EmailProviderInfo {
	private String domain;
	private String imapHost;
	private LoginType loginType;
	private Integer maxConcurrentConnections;
	private Integer sizeMB;
	private Integer attachmentSizeMB;

	public EmailProviderInfo(String domain, String imapHost, LoginType loginType, Integer maxConcurrentConnections, Integer sizeMB, Integer attachmentSizeMB) {
		this.domain = domain;
		this.imapHost = imapHost;
		this.loginType = loginType;
		this.maxConcurrentConnections = maxConcurrentConnections;
		this.sizeMB = sizeMB;
		this.attachmentSizeMB = attachmentSizeMB;
	}

	public EmailProviderInfo() {
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getImapHost() {
		return this.imapHost;
	}

	public void setImapHost(String imapHost) {
		this.imapHost = imapHost;
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




