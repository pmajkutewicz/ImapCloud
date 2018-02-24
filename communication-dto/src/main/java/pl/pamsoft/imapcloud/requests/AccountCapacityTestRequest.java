package pl.pamsoft.imapcloud.requests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.AccountDto;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class AccountCapacityTestRequest {
	private AccountDto selectedAccount;

	public AccountCapacityTestRequest(AccountDto selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

	public AccountCapacityTestRequest() {
	}

	public AccountDto getSelectedAccount() {
		return this.selectedAccount;
	}

	public void setSelectedAccount(AccountDto selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

}
