package pl.pamsoft.imapcloud.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.websocket.OnFailedInitNotification;
import pl.pamsoft.imapcloud.websocket.ServerConnection;
import pl.pamsoft.imapcloud.websocket.ServerConnectionImpl;

import javax.inject.Singleton;

public class DefaultModule extends AbstractModule {

	private OnFailedInitNotification notification;

	public DefaultModule(OnFailedInitNotification notification) {
		this.notification = notification;
	}

	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	ServerConnection get() {
		return new ServerConnectionImpl(notification);
	}

	@Provides
	@Singleton
	AccountRestClient getAccountRestClient() {
		return new AccountRestClient("http://localhost:9000/");
	}

}
