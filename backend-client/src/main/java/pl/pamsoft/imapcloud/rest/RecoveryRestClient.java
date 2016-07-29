package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.requests.RecoverRequest;
import pl.pamsoft.imapcloud.requests.StartRecoveryRequest;
import pl.pamsoft.imapcloud.responses.RecoveryResultsResponse;

import java.util.Set;

public class RecoveryRestClient extends AbstractRestClient {

	private static final String START_RECOVERY = "recovery/start";
	private static final String GET_RESULTS = "recovery/results";
	private static final String RECOVER_FILES = "recovery/recover";

	public RecoveryRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void startAccountRecovery(AccountDto selectedAccount, RequestCallback<Void> callback) {
		sendPost(START_RECOVERY, new StartRecoveryRequest(selectedAccount), callback);
	}

	public void getResults(RequestCallback<RecoveryResultsResponse> callback) {
		sendGet(GET_RESULTS, RecoveryResultsResponse.class, callback);
	}

	public void recover(String taskId, Set<String> uniqueFileIds, RequestCallback<Void> callback) {
		sendPost(RECOVER_FILES, new RecoverRequest(taskId, uniqueFileIds), callback);
	}

}
