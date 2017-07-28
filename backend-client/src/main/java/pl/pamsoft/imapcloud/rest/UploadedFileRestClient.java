package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.responses.UploadedFileChunksResponse;
import pl.pamsoft.imapcloud.responses.UploadedFilesResponse;

public class UploadedFileRestClient extends AbstractRestClient {

	private static final String FIND_ALL_FILES = "uploaded/files";
	private static final String RESUME_FILE = "uploaded/resume";
	private static final String VERIFY_FILE = "uploaded/verify";
	private static final String DELETE_FILE = "uploaded/delete";
	private static final String FIND_ALL_FILE_CHUNKS = "uploaded/chunks";

	public UploadedFileRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getUploadedFiles(RequestCallback<UploadedFilesResponse> callback) {
		sendGet(FIND_ALL_FILES, UploadedFilesResponse.class, callback);
	}

	public void getUploadedFileChunks(String fileId, RequestCallback<UploadedFileChunksResponse> callback) {
		sendGet(FIND_ALL_FILE_CHUNKS, UploadedFileChunksResponse.class, "fileId", fileId, callback);
	}

	public void verifyFile(String fileId, RequestCallback<Void> callback) {
		sendGet(VERIFY_FILE, "fileId", fileId, callback);
	}

	public void resumeFile(String fileId, RequestCallback<Void> callback) {
		sendGet(RESUME_FILE, "fileId", fileId, callback);
	}

	public void deleteFile(String fileId, RequestCallback<Void> callback) {
		sendGet(DELETE_FILE, "fileId", fileId, callback);
	}

}
