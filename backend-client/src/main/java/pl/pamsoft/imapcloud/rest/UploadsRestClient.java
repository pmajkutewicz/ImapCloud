package pl.pamsoft.imapcloud.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.requests.StartUploadRequest;

import java.io.IOException;
import java.util.List;

public class UploadsRestClient extends AbstractRestClient {

	private static final Logger LOG = LoggerFactory.getLogger(UploadsRestClient.class);
	private static final String START_UPLOADS = "/uploads/start";
	private final String endpoint;

	public UploadsRestClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public void startUpload(List<FileDto> selectedFiles, AccountDto selectedAccount) throws IOException {
		try {
			HttpResponse<JsonNode> httpResponse = Unirest.post(endpoint + START_UPLOADS)
				.body(new StartUploadRequest(selectedFiles, selectedAccount))
				.asJson();
			LOG.debug(httpResponse.toString());
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}
}
