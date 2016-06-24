package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.requests.StartRecoveryRequest;

public class RecoveryRestClient extends AbstractRestClient {

	private static final String START_RECOVERY = "recovery/start";

	public RecoveryRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void startUpload(AccountDto selectedAccount, RequestCallback<Void> callback) {
		sendPost(START_RECOVERY, new StartRecoveryRequest(selectedAccount), callback);
	}

}
