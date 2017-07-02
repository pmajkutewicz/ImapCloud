package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.AccountInfo;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class AccountProviderInfoResponse extends AbstractResponse {
	private List<AccountInfo> accountProviders;

	public AccountProviderInfoResponse(List<AccountInfo> accountProviders) {
		this.accountProviders = accountProviders;
	}

	public AccountProviderInfoResponse() {
	}

	public List<AccountInfo> getAccountProviders() {
		return this.accountProviders;
	}

	public void setAccountProviders(List<AccountInfo> accountProviders) {
		this.accountProviders = accountProviders;
	}
}
