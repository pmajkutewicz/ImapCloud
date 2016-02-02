package pl.pamsoft.imapcloud.websocket;

import javafx.concurrent.Task;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

public class ServerConnectionImpl implements ServerConnection {

	private AccountClient accountClient;
	private OnFailedInitNotification notification;
	private Task<Void> task = new Task<Void>() {
		@Override
		protected Void call() {
			try {
				String dest = "ws://localhost:9000/account";
				accountClient = new AccountClient();
				WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				container.connectToServer(accountClient, new URI(dest));
				accountClient.getLatch().await();
				return null;
			} catch (InterruptedException | DeploymentException | URISyntaxException | IOException e) {
				notification.initializationFailedNotification(e);
			}
			return null;
		}
	};

	public ServerConnectionImpl(OnFailedInitNotification notification) {
		this.notification = notification;
	}

	@Override
	public AccountClient getAccountSession() {
		return accountClient;
	}

	@Override
	public void start() throws ConnectException {
		new Thread(task,"WebSocketConnectionThread").start();
	}

	@Override
	public void close() {
		accountClient.close();
	}
}
