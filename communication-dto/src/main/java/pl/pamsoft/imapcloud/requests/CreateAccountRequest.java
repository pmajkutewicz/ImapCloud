package pl.pamsoft.imapcloud.requests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class CreateAccountRequest {
	private String username;
	private String password;
	private String cryptoKey;
	private EmailProviderInfo selectedEmailProvider;

	public CreateAccountRequest(String username, String password, String cryptoKey, EmailProviderInfo selectedEmailProvider) {
		this.username = username;
		this.password = password;
		this.cryptoKey = cryptoKey;
		this.selectedEmailProvider = selectedEmailProvider;
	}

	public CreateAccountRequest() {
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCryptoKey() {
		return this.cryptoKey;
	}

	public void setCryptoKey(String cryptoKey) {
		this.cryptoKey = cryptoKey;
	}

	public EmailProviderInfo getSelectedEmailProvider() {
		return this.selectedEmailProvider;
	}

	public void setSelectedEmailProvider(EmailProviderInfo selectedEmailProvider) {
		this.selectedEmailProvider = selectedEmailProvider;
	}
}
