package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.EmailProviderInfo;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.EmailProviderInfoResponse;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;

public class AccountRestClient extends AbstractRestClient {

	private static final String LIST_EMAIL_PROVIDERS = "accounts/emailProviders";
	private static final String CREATE_ACCOUNT = "accounts";

	public AccountRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getAvailableEmailAccounts(RequestCallback<EmailProviderInfoResponse> callback) {
		sendGet(LIST_EMAIL_PROVIDERS, EmailProviderInfoResponse.class, callback);
	}

	public void createAccount(EmailProviderInfo selectedEmailProvider, String username, String password, RequestCallback<Void> callback) {
		sendPost(CREATE_ACCOUNT, new CreateAccountRequest(username, password, selectedEmailProvider), callback);
	}

	public void listAccounts(RequestCallback<ListAccountResponse> callback) {
		sendGet(CREATE_ACCOUNT, ListAccountResponse.class, callback);
	}
}
