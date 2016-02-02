package pl.pamsoft.imapcloud.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpStatus;

import java.io.IOException;

abstract class AbstractRestClient {

	boolean initialized;
	ObjectMapper objectMapper = new JacksonObjectMapper();

	AbstractRestClient() {
		initUnirest();
	}

	void throwExceptionIfNotValidResponse(HttpResponse response) throws IOException {
		if (HttpStatus.SC_OK != response.getStatus()) {
			throw new IOException(response.getStatusText());
		}
	}

	boolean isSuccessResponse(HttpResponse response) {
		return HttpStatus.SC_OK == response.getStatus();
	}

	private void initUnirest() {
		if (!initialized) {
			Unirest.setObjectMapper(objectMapper);
			Unirest.setDefaultHeader("Accept", "application/json");
			Unirest.setDefaultHeader("Content-Type", "application/json");
			initialized = true;
		}
	}
}
