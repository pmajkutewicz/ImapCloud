package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.responses.GetHomeDirResponse;
import pl.pamsoft.imapcloud.responses.ListFilesInDirResponse;

import java.io.IOException;

public class FilesRestClient extends AbstractRestClient {

	private static final String GET_HOME_DIR = "files/homeDir";
	private static final String LIST_FILES_IN_DIR = "files";

	public FilesRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	@Deprecated
	public GetHomeDirResponse getHomeDir() throws IOException {
		return sendGet(GET_HOME_DIR, GetHomeDirResponse.class);
	}

	@Deprecated
	public ListFilesInDirResponse listDir(String dir) throws IOException {
		return sendGet(LIST_FILES_IN_DIR, ListFilesInDirResponse.class, "dir", dir);
	}
}
