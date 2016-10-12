package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class EmailProviderInfoResponse extends AbstractResponse {
	private List<EmailProviderInfo> emailProviders;

	public EmailProviderInfoResponse(List<EmailProviderInfo> emailProviders) {
		this.emailProviders = emailProviders;
	}

	public EmailProviderInfoResponse() {
	}

	public List<EmailProviderInfo> getEmailProviders() {
		return this.emailProviders;
	}

	public void setEmailProviders(List<EmailProviderInfo> emailProviders) {
		this.emailProviders = emailProviders;
	}
}
