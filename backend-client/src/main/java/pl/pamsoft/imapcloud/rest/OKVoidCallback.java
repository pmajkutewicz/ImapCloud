package pl.pamsoft.imapcloud.rest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class OKVoidCallback implements Callback {

	private RequestCallback<Void> requestCallback;

	public OKVoidCallback(RequestCallback<Void> requestCallback) {
		this.requestCallback = requestCallback;
	}

	@Override
	public void onFailure(Call call, IOException e) {
		requestCallback.onFailure(e);
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException {
		requestCallback.onSuccess(null);
	}
}
