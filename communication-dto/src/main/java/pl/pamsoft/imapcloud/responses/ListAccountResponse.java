package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.AccountDto;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class ListAccountResponse extends AbstractResponse {
	private List<AccountDto> account;

	public ListAccountResponse(List<AccountDto> account) {
		this.account = account;
	}

	public ListAccountResponse() {
	}

	public List<AccountDto> getAccount() {
		return this.account;
	}

	public void setAccount(List<AccountDto> account) {
		this.account = account;
	}
}
