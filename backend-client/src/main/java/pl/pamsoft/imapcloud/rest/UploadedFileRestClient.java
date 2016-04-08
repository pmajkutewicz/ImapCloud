package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.responses.UploadedFileChunksResponse;
import pl.pamsoft.imapcloud.responses.UploadedFilesResponse;

import java.io.IOException;

public class UploadedFileRestClient extends AbstractRestClient {

	private static final String FIND_ALL_FILES = "uploaded/files";
	private static final String VERIFY_FILE = "uploaded/verify";
	private static final String FIND_ALL_FILE_CHUNKS = "uploaded/chunks";

	public UploadedFileRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public UploadedFilesResponse getUploadedFiles() throws IOException {
		return sendGet(FIND_ALL_FILES, UploadedFilesResponse.class);
	}

	public UploadedFileChunksResponse getUploadedFileChunks(String fileId) throws IOException {
		return sendGet(FIND_ALL_FILE_CHUNKS, UploadedFileChunksResponse.class, "fileId", fileId);
	}

	public void verifyFile(String fileId) throws IOException {
		sendGet(VERIFY_FILE, "fileId", fileId);
	}
}
