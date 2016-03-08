package pl.pamsoft.imapcloud.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.FilesRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;
import pl.pamsoft.imapcloud.websocket.PerformanceDataClient;
import pl.pamsoft.imapcloud.websocket.TaskProgressClient;

import javax.inject.Singleton;

public class DefaultModule extends AbstractModule {

	private static final String ENDPOINT = "http://localhost:9000/";

	private final String endpoint;
	private final String username;
	private final String password;

	public DefaultModule(String endpoint, String username, String password) {
		this.endpoint = endpoint;
		this.username = username;
		this.password = password;
	}

	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	PerformanceDataClient getPerformanceDataClient() {
		return new PerformanceDataClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	TaskProgressClient getTaskProgressClient() {
		return new TaskProgressClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	AccountRestClient getAccountRestClient() {
		return new AccountRestClient("http://" + endpoint, username, password);
	}

	@Provides
	@Singleton
	FilesRestClient getFilesRestClient() {
		return new FilesRestClient("http://" + endpoint, username, password);
	}

	@Provides
	@Singleton
	UploadsRestClient getUploadRestClient() {
		return new UploadsRestClient("http://" + endpoint, username, password);
	}

	@Provides
	@Singleton
	Utils getUtils() {
		return new Utils();
	}
}
