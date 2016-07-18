package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.requests.StartRecoveryRequest;
import pl.pamsoft.imapcloud.responses.RecoveryResultsResponse;

public class RecoveryRestClient extends AbstractRestClient {

	private static final String START_RECOVERY = "recovery/start";
	private static final String GET_RESULTS = "recovery/results";

	public RecoveryRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void startUpload(AccountDto selectedAccount, RequestCallback<Void> callback) {
		sendPost(START_RECOVERY, new StartRecoveryRequest(selectedAccount), callback);
	}

	public void getResults(RequestCallback<RecoveryResultsResponse> callback) {
		sendGet(GET_RESULTS, RecoveryResultsResponse.class, callback);
	}

}
