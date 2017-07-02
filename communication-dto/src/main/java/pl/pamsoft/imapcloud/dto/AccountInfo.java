package pl.pamsoft.imapcloud.dto;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;
import java.util.Set;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class AccountInfo {

	private static Set<String> standardFields = ImmutableSet.of("type", "host", "accountSizeMB", "maxFileSizeMB", "maxConcurrentConnections");
	private String type;
	private String host;
	private Integer accountSizeMB;
	private Integer maxFileSizeMB;
	private Integer maxConcurrentConnections;
	private Map<String, String> additionalProperties;

	public AccountInfo(String type, String host, Integer maxConcurrentConnections, Integer accountSizeMB, Integer maxFileSizeMB, Map<String, String> additionalProperties) {
		this.type = type;
		this.host = host;
		this.maxConcurrentConnections = maxConcurrentConnections;
		this.accountSizeMB = accountSizeMB;
		this.maxFileSizeMB = maxFileSizeMB;
		this.additionalProperties = additionalProperties;
	}

	public AccountInfo() {
	}

	public static Set<String> getStandardFields() {
		return standardFields;
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

	public Integer getMaxFileSizeMB() {
		return this.maxFileSizeMB;
	}

	public void setMaxFileSizeMB(Integer maxFileSizeMB) {
		this.maxFileSizeMB = maxFileSizeMB;
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




