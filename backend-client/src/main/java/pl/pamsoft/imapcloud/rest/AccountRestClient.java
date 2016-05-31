package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.EmailProviderInfoResponse;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;

import java.io.IOException;
import java.util.List;

public class AccountRestClient extends AbstractRestClient {

	private static final String LIST_EMAIL_PROVIDERS = "accounts/emailProviders";
	private static final String CREATE_ACCOUNT = "accounts";

	public AccountRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	@Deprecated
	public EmailProviderInfoResponse getAvailableEmailAccounts() throws IOException {
		return sendGet(LIST_EMAIL_PROVIDERS, EmailProviderInfoResponse.class);
	}

	@Deprecated
	public void createAccount(EmailProviderInfo selectedEmailProvider, String username, String password) throws IOException {
		sendPost(CREATE_ACCOUNT, new CreateAccountRequest(username, password, selectedEmailProvider));
	}

	@Deprecated
	public List<AccountDto> listAccounts() throws IOException {
		return sendGet(CREATE_ACCOUNT, ListAccountResponse.class).getAccount();
	}
}
