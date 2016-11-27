package pl.pamsoft.imapcloud.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.concurrent.TimeUnit;

abstract class AbstractRestClient {

	private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
	private static final int TIMEOUT = 5;
	public static final int MAX_REQUESTS_DISPATCHER = 10;
	public static final int MAX_IDLE_CONNECTIONS = 10;
	public static final int KEEP_ALIVE_DURATION = 2;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String host;
	private final int port;
	private final Authenticator authenticator;
	private OkHttpClient client;

	AbstractRestClient(String endpoint, String username, String pass) {
		String[] end = endpoint.split(":");
		this.host = end[0];
		this.port = Integer.parseInt(end[1]);
		this.authenticator = (route, response) -> response.request().newBuilder().header("Authorization", Credentials.basic(username, pass)).build();
	}

	protected <T> void sendGet(String url, Class<T> cls, RequestCallback<T> requestCallback) {
		sendGet(buildUrl(url), new OKDefaultCallback<>(cls, requestCallback));
	}

	protected <T> void sendGet(String url, Class<T> cls, String paramName, String paramValue, RequestCallback<T> requestCallback) {
		sendGet(buildUrl(url, paramName, paramValue), new OKDefaultCallback<>(cls, requestCallback));
	}

	protected void sendGet(String url, String paramName, String paramValue, RequestCallback<Void> callback) {
		sendGet(buildUrl(url, paramName, paramValue), new OKVoidCallback(callback));
	}

	protected void sendPost(String url, Object pojo, RequestCallback<Void> callback) {
		sendPost(buildUrl(url), pojo, new OKVoidCallback(callback));
	}

	private void sendGet(HttpUrl httpUrl, Callback callback) {
		send(getRequest().url(httpUrl.url()).build(), callback);
	}

	@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE")
	private void sendPost(HttpUrl httpUrl, Object pojo, Callback callback) {
		Request req = null;
		try {
			RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, objectMapper.writeValueAsString(pojo));
			req = getRequest().url(httpUrl.url()).post(requestBody).build();
			send(req, callback);
		} catch (JsonProcessingException e) {
			callback.onFailure(null != req ? getClient().newCall(req) : null, e);
		}
	}

	private void send(Request req, Callback callback) {
		getClient().newCall(req).enqueue(callback);
	}

	private HttpUrl buildUrl(String url) {
		return new HttpUrl.Builder().scheme("http").host(host).port(port).addPathSegments(url).build();
	}

	private HttpUrl buildUrl(String url, String paramName, String paramValue) {
		return new HttpUrl.Builder().scheme("http").host(host).port(port).addPathSegments(url).addQueryParameter(paramName, paramValue).build();
	}

	private OkHttpClient getClient() {
		if (null == client) {
			Dispatcher dispatcher = new Dispatcher();
			ConnectionPool pool = new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.MINUTES);
			dispatcher.setMaxRequests(MAX_REQUESTS_DISPATCHER);
			client = new OkHttpClient.Builder()
				.authenticator(authenticator)
				.connectionPool(pool)
				.dispatcher(dispatcher)
				.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
				.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
		}
		return client;
	}

	private Request.Builder getRequest() {
		return new Request.Builder()
			.header("User-Agent", "IC JavaFX")
			.addHeader("Accept", "application/json")
			.addHeader("Content-Type", "application/json");
	}
}
