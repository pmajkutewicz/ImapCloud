package pl.pamsoft.imapcloud.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.responses.UploadedFilesResponse;

import java.io.IOException;

public class UploadedFileRestClient extends AbstractRestClient {

	private static final Logger LOG = LoggerFactory.getLogger(UploadedFileRestClient.class);
	private static final String FIND_ALL = "/uploaded";

	public UploadedFileRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public UploadedFilesResponse getUploadedFiles() throws IOException {
		try {
			HttpResponse<UploadedFilesResponse> response = Unirest.get(endpoint + FIND_ALL).basicAuth(bAuthUsername, bAuthPassword)
				.asObject(UploadedFilesResponse.class);
			throwExceptionIfNotValidResponse(response);
			return response.getBody();
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}
}
