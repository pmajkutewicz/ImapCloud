package pl.pamsoft.imapcloud.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.DownloadsRestClient;
import pl.pamsoft.imapcloud.rest.FilesRestClient;
import pl.pamsoft.imapcloud.rest.GitStatusRestClient;
import pl.pamsoft.imapcloud.rest.MonitoringRestClient;
import pl.pamsoft.imapcloud.rest.RecoveryRestClient;
import pl.pamsoft.imapcloud.rest.TaskProgressRestClient;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;
import pl.pamsoft.imapcloud.tools.PlatformTools;

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
	protected AccountRestClient getAccountRestClient() {
		return new AccountRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected FilesRestClient getFilesRestClient() {
		return new FilesRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected UploadsRestClient getUploadRestClient() {
		return new UploadsRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected UploadedFileRestClient getUploadedFileRestClient() {
		return new UploadedFileRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected DownloadsRestClient getDownloadRestClient() {
		return new DownloadsRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected RecoveryRestClient getRecoveryRestClient() {
		return new RecoveryRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected GitStatusRestClient getGitStatusRestClient() {
		return new GitStatusRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected MonitoringRestClient getMonitoringRestClient() {
		return new MonitoringRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected TaskProgressRestClient getTasksProgressRestClient() {
		return new TaskProgressRestClient(endpoint, username, password);
	}

	@Provides
	@Singleton
	protected Utils getUtils() {
		return new Utils();
	}

	@Provides
	@Singleton
	protected PlatformTools getPlatformTools() {
		return new PlatformTools();
	}
}
