package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.requests.AccountCapacityTestRequest;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.AccountProviderInfoResponse;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;

public class AccountRestClient extends AbstractRestClient {

	private static final String TEST_CAPACITY = "accounts/testCapacity";
	private static final String LIST_ACCOUNT_PROVIDERS = "accounts/accountProviders";
	private static final String CREATE_ACCOUNT = "accounts";

	public AccountRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getAvailableAccounts(RequestCallback<AccountProviderInfoResponse> callback) {
		sendGet(LIST_ACCOUNT_PROVIDERS, AccountProviderInfoResponse.class, callback);
	}

	public void createAccount(AccountInfo selectedEmailProvider, String username, String password, String cryptoKey, RequestCallback<Void> callback) {
		sendPost(CREATE_ACCOUNT, new CreateAccountRequest(username, password, cryptoKey, selectedEmailProvider), callback);
	}

	public void testCapacity(AccountDto accountDto, RequestCallback<Void> callback) {
		sendPost(TEST_CAPACITY, new AccountCapacityTestRequest(accountDto), callback);
	}

	public void listAccounts(RequestCallback<ListAccountResponse> callback) {
		sendGet(CREATE_ACCOUNT, ListAccountResponse.class, callback);
	}
}
