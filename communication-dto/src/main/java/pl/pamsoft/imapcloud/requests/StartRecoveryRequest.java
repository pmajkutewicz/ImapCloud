package pl.pamsoft.imapcloud.requests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.AccountDto;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class StartRecoveryRequest {
	private AccountDto selectedAccount;

	public StartRecoveryRequest(AccountDto selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

	public StartRecoveryRequest() {
	}

	public AccountDto getSelectedAccount() {
		return this.selectedAccount;
	}

	public void setSelectedAccount(AccountDto selectedAccount) {
		this.selectedAccount = selectedAccount;
	}
}
