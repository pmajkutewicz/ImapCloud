package pl.pamsoft.imapcloud.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.responses.GetHomeDirResponse;
import pl.pamsoft.imapcloud.responses.ListFilesInDirResponse;

import java.io.IOException;

public class FilesRestClient extends AbstractRestClient {

	private static final Logger LOG = LoggerFactory.getLogger(FilesRestClient.class);
	private static final String GET_HOME_DIR = "/files/homeDir";
	private static final String LIST_FILES_IN_DIR = "/files";
	private final String endpoint;

	public FilesRestClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public GetHomeDirResponse getHomeDir() throws IOException {
		try {
			HttpResponse<GetHomeDirResponse> response = Unirest.get(endpoint + GET_HOME_DIR)
				.asObject(GetHomeDirResponse.class);
			throwExceptionIfNotValidResponse(response);
			return response.getBody();
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}

	public ListFilesInDirResponse listDir(String dir) throws IOException {
		try {
			HttpResponse<ListFilesInDirResponse> response = Unirest.get(endpoint + LIST_FILES_IN_DIR)
				.queryString("dir", dir)
				.asObject(ListFilesInDirResponse.class);
			throwExceptionIfNotValidResponse(response);
			return response.getBody();
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}
}
