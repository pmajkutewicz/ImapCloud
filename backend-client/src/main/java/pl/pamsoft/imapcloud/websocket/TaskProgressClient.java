package pl.pamsoft.imapcloud.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TaskProgressClient extends Endpoint implements MessageHandler.Whole<String> {

	private static final Logger LOG = LoggerFactory.getLogger(TaskProgressClient.class);
	private final String endpoint;
	private final String username;
	private final String password;
	ObjectMapper mapper = new ObjectMapper();
	private CountDownLatch latch = new CountDownLatch(1);
	private List<TaskProgressEventListener> listeners = new ArrayList<>();
	private Session websocketSession;

	public TaskProgressClient(String endpoint, String username, String password) {
		this.endpoint = String.format("ws://%s/tasks", endpoint);
		this.username = username;
		this.password = password;
	}

	public void addListener(TaskProgressEventListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(TaskProgressEventListener listener) {
		this.listeners.remove(listener);
	}

	public void connect() throws IOException, URISyntaxException, InterruptedException, DeploymentException {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, ClientEndpointConfig.Builder.create().configurator(new AuthorizationConfigurator(username, password)).build(), new URI(endpoint));
		getLatch().await();
	}

	public void disconnect() throws IOException, URISyntaxException, InterruptedException, DeploymentException {
		if (websocketSession.isOpen()) {
			websocketSession.close();
		}
	}

	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("Connected to task progress server");
		this.websocketSession = session;
		websocketSession.addMessageHandler(this);
		latch.countDown();
	}

	public void onMessage(String message) {
		for (TaskProgressEventListener listener : listeners) {
			try {
				TaskProgressEvent eventData = mapper.readValue(message, TaskProgressEvent.class);
				listener.onEventReceived(eventData);
			} catch (IOException e) {
				LOG.warn("Can't deserialize json", e);
			}
		}
		LOG.info("Message received from server: {}", message);
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		LOG.info("Closing a WebSocket due to {}", closeReason.getReasonPhrase());
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void sendMessage(String str) throws IOException {
		websocketSession.getBasicRemote().sendText(str);
	}

	public void close() {
		try {
			websocketSession.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
