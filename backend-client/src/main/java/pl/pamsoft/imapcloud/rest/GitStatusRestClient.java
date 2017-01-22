package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.GitRepositoryState;

public class GitStatusRestClient extends AbstractRestClient {

	private static final String GIT_STATUS = "git/status";

	public GitStatusRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getGitStatus(RequestCallback<GitRepositoryState> callback) {
		sendGet(GIT_STATUS, GitRepositoryState.class, callback);
	}

}
