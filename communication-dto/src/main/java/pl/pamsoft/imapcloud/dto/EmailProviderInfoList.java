package pl.pamsoft.imapcloud.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class EmailProviderInfoList {
	private List<EmailProviderInfo> emailProviders;

	public EmailProviderInfoList(List<EmailProviderInfo> emailProviders) {
		this.emailProviders = emailProviders;
	}

	public EmailProviderInfoList() {
	}

	public List<EmailProviderInfo> getEmailProviders() {
		return this.emailProviders;
	}

	public void setEmailProviders(List<EmailProviderInfo> emailProviders) {
		this.emailProviders = emailProviders;
	}
}




