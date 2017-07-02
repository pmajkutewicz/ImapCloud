package pl.pamsoft.imapcloud.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class AccountProviderInfoList {
	private List<AccountInfo> accountProviders;

	public AccountProviderInfoList(List<AccountInfo> accountProviders) {
		this.accountProviders = accountProviders;
	}

	public AccountProviderInfoList() {
	}

	public List<AccountInfo> getAccountProviders() {
		return this.accountProviders;
	}

	public void setAccountProviders(List<AccountInfo> accountProviders) {
		this.accountProviders = accountProviders;
	}
}




