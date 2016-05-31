package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.requests.StartDownloadRequest;

import java.io.IOException;

public class DownloadsRestClient extends AbstractRestClient {

	private static final String START_UPLOADS = "downloads/start";

	public DownloadsRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	@Deprecated
	public void startDownload(UploadedFileDto fileToDownload, FileDto destDir) throws IOException {
		sendPost(START_UPLOADS, new StartDownloadRequest(fileToDownload, destDir));
	}
}
