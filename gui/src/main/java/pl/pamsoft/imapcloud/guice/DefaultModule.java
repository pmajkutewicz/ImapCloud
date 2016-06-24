package pl.pamsoft.imapcloud.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.DownloadsRestClient;
import pl.pamsoft.imapcloud.rest.FilesRestClient;
import pl.pamsoft.imapcloud.rest.RecoveryRestClient;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;
import pl.pamsoft.imapcloud.websocket.PerformanceDataClient;
import pl.pamsoft.imapcloud.websocket.TaskProgressClient;

import javax.inject.Singleton;

public class DefaultModule extends AbstractModule {

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
		return new AccountRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	FilesRestClient getFilesRestClient() {
		return new FilesRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	UploadsRestClient getUploadRestClient() {
		return new UploadsRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	UploadedFileRestClient getUploadedFileRestClient() {
		return new UploadedFileRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	DownloadsRestClient getDownloadRestClient() {
		return new DownloadsRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	RecoveryRestClient getRecoveryRestClient() {
		return new RecoveryRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	Utils getUtils() {
		return new Utils();
	}
}
