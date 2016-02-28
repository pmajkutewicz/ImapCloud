package pl.pamsoft.imapcloud.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.FilesRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;
import pl.pamsoft.imapcloud.websocket.OnFailedInitNotification;
import pl.pamsoft.imapcloud.websocket.PerformanceDataClient;
import pl.pamsoft.imapcloud.websocket.TaskProgressClient;

import javax.inject.Singleton;

public class DefaultModule extends AbstractModule {

	private static final String ENDPOINT = "http://localhost:9000/";
	private OnFailedInitNotification notification;

	public DefaultModule(OnFailedInitNotification notification) {
		this.notification = notification;
	}

	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	PerformanceDataClient getPerformanceDataClient() {
		return new PerformanceDataClient();
	}

	@Provides
	@Singleton
	TaskProgressClient getTaskProgressClient() {
		return new TaskProgressClient();
	}

	@Provides
	@Singleton
	AccountRestClient getAccountRestClient() {
		return new AccountRestClient(ENDPOINT);
	}

	@Provides
	@Singleton
	FilesRestClient getFilesRestClient() {
		return new FilesRestClient(ENDPOINT);
	}

	@Provides
	@Singleton
	UploadsRestClient getUploadRestClient() {
		return new UploadsRestClient(ENDPOINT);
	}

	@Provides
	@Singleton
	Utils getUtils() {
		return new Utils();
	}
}
