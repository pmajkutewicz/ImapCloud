package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.responses.GetHomeDirResponse;
import pl.pamsoft.imapcloud.responses.ListFilesInDirResponse;

public class FilesRestClient extends AbstractRestClient {

	private static final String GET_HOME_DIR = "files/homeDir";
	private static final String LIST_FILES_IN_DIR = "files";

	public FilesRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getHomeDir(RequestCallback<GetHomeDirResponse> callback) {
		sendGet(GET_HOME_DIR, GetHomeDirResponse.class, callback);
	}

	public void listDir(String dir, RequestCallback<ListFilesInDirResponse> callback) {
		sendGet(LIST_FILES_IN_DIR, ListFilesInDirResponse.class, "dir", dir, callback);
	}
}
