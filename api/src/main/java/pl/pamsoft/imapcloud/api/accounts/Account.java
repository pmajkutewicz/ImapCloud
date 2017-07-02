package pl.pamsoft.imapcloud.api.accounts;

import java.util.Map;

public interface Account {
	String getLogin();

	String getType();

	String getHost();

	String getPassword();

	Integer getMaxConcurrentConnections();

	Integer getAccountSizeMB();

	Integer getAttachmentSizeMB();

	String getCryptoKey();

	Map<String, String> getAdditionalProperties();

	String getProperty(String name);
}
