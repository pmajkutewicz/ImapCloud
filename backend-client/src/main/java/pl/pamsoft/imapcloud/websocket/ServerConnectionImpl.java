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

	private PerformanceDataClient performanceDataClient;
	private OnFailedInitNotification notification;
	private Task<Void> task = new Task<Void>() {
		@Override
		protected Void call() {
			try {
				String dest = "ws://localhost:9000/performance";
				performanceDataClient = new PerformanceDataClient();
				WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				container.connectToServer(performanceDataClient, new URI(dest));
				performanceDataClient.getLatch().await();
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
	public PerformanceDataClient getPerformanceDataClient() {
		return performanceDataClient;
	}

	@Override
	public void start() throws ConnectException {
		new Thread(task,"WebSocketConnectionThread").start();
	}

	@Override
	public void close() {
		performanceDataClient.close();
	}
}
