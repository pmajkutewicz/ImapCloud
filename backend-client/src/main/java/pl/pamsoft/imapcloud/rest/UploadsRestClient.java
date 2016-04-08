package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.requests.StartUploadRequest;

import java.io.IOException;
import java.util.List;

public class UploadsRestClient extends AbstractRestClient {

	private static final String START_UPLOADS = "uploads/start";

	public UploadsRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void startUpload(List<FileDto> selectedFiles, AccountDto selectedAccount) throws IOException {
		sendPost(START_UPLOADS, new StartUploadRequest(selectedFiles, selectedAccount, true));
	}
}
