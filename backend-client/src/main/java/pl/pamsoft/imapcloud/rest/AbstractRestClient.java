package pl.pamsoft.imapcloud.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

abstract class AbstractRestClient {

	private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String host;
	private final int port;
	private final Authenticator authenticator;

	AbstractRestClient(String endpoint, String username, String pass) {
		String[] end = endpoint.split(":");
		this.host = end[0];
		this.port = Integer.parseInt(end[1]);
		this.authenticator = (route, response) -> response.request().newBuilder().header("Authorization", Credentials.basic(username, pass)).build();
	}

	@Deprecated
	<T> T sendGet(String url, Class<T> cls) throws IOException {
		return objectMapper.readValue(sendGet(buildUrl(url)).body().string(), cls);
	}

	<T> void sendGetAsync(String url, Class<T> cls, RequestCallback<T> requestCallback) throws IOException {
		sendGetAsync(buildUrl(url), new OKDefaultCallback<T>(cls, requestCallback));
	}

	@Deprecated
	<T> T sendGet(String url, Class<T> cls, String paramName, String paramValue) throws IOException {
		return objectMapper.readValue(sendGet(buildUrl(url, paramName, paramValue)).body().string(), cls);
	}

	<T> void sendGetAsync(String url, Class<T> cls, String paramName, String paramValue, RequestCallback<T> requestCallback) {
		sendGetAsync(buildUrl(url, paramName, paramValue), new OKDefaultCallback<T>(cls, requestCallback));
	}

	@Deprecated
	Response sendGet(String url, String paramName, String paramValue) throws IOException {
		return sendGet(buildUrl(url, paramName, paramValue));
	}

	@Deprecated
	Response sendPost(String url, Object pojo) throws IOException {
		return sendPost(buildUrl(url), pojo);
	}

	@Deprecated
	private Response sendGet(HttpUrl httpUrl) throws IOException {
		return send(getRequest().url(httpUrl.url()).build());
	}

	private void sendGetAsync(HttpUrl httpUrl, Callback callback) {
		sendAsync(getRequest().url(httpUrl.url()).build(), callback);
	}

	@Deprecated
	private Response sendPost(HttpUrl httpUrl, Object pojo) throws IOException {
		RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, objectMapper.writeValueAsString(pojo));
		Request req = getRequest().url(httpUrl.url()).post(requestBody).build();
		return send(req);
	}

	@Deprecated
	private Response send(Request req) throws IOException {
		Response response = getClient().newCall(req).execute();
		throwExceptionIfNotValidResponse(response);
		return response;
	}

	private void sendAsync(Request req, Callback callback) {
		getClient().newCall(req).enqueue(callback);
	}

	private HttpUrl buildUrl(String url) {
		return new HttpUrl.Builder().scheme("http").host(host).port(port).addPathSegments(url).build();
	}

	private HttpUrl buildUrl(String url, String paramName, String paramValue) {
		return new HttpUrl.Builder().scheme("http").host(host).port(port).addPathSegments(url).addQueryParameter(paramName, paramValue).build();
	}

	private OkHttpClient getClient() {
		return new OkHttpClient.Builder()
			.authenticator(authenticator)
			.connectTimeout(2, TimeUnit.SECONDS)
			.writeTimeout(2, TimeUnit.SECONDS)
			.readTimeout(2, TimeUnit.SECONDS).build();
	}

	private Request.Builder getRequest() {
		return new Request.Builder()
			.header("User-Agent", "IC JavaFX")
			.addHeader("Accept", "application/json")
			.addHeader("Content-Type", "application/json");
	}

	private void throwExceptionIfNotValidResponse(Response response) throws IOException {
		if (!response.isSuccessful()) {
			throw new IOException(response.body().string());
		}
	}
}
