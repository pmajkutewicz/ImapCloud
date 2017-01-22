package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.responses.GetFeaturesResponse;

import java.io.IOException;

public class IMAPCloudFeaturesClient extends AbstractRestClient {

	private static final String GET_FEATURES = "features";

	public IMAPCloudFeaturesClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public GetFeaturesResponse getFeatures() throws IOException {
		return sendGet(GET_FEATURES, GetFeaturesResponse.class);
	}

}
