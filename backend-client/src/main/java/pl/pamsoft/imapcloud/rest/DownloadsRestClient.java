package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.requests.StartDownloadRequest;

public class DownloadsRestClient extends AbstractRestClient {

	private static final String START_UPLOADS = "downloads/start";

	public DownloadsRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void startDownload(UploadedFileDto fileToDownload, FileDto destDir, RequestCallback<Void> callback) {
		sendPost(START_UPLOADS, new StartDownloadRequest(fileToDownload, destDir), callback);
	}
}
