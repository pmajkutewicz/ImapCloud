package pl.pamsoft.imapcloud.rest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pl.pamsoft.imapcloud.rest.json.MapperHolder;

import java.io.IOException;

public class OKDefaultCallback<T> implements Callback {

	private Class<T> cls;
	private RequestCallback<T> requestCallback;

	public OKDefaultCallback(Class<T> cls, RequestCallback<T> requestCallback) {
		this.cls = cls;
		this.requestCallback = requestCallback;
	}

	@Override
	public void onFailure(Call call, IOException e) {
		requestCallback.onFailure(e);
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException {
		T responseData = MapperHolder.OBJECT_MAPPER.readValue(response.body().string(), cls);
		throwExceptionIfNotValidResponse(response);
		requestCallback.onSuccess(responseData);
	}

	private void throwExceptionIfNotValidResponse(Response response) throws IOException {
		if (!response.isSuccessful()) {
			throw new IOException(response.body().string());
		}
	}
}
