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

public abstract class AbstractWebsocketClient<T> extends Endpoint implements MessageHandler.Whole<String> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractWebsocketClient.class);
	private final String endpoint;
	private final String username;
	private final String password;
	private ObjectMapper mapper = new ObjectMapper();
	private CountDownLatch latch = new CountDownLatch(1);
	private List<T> listeners = new ArrayList<>();
	private Session websocketSession;

	AbstractWebsocketClient(String endpoint, String path, String username, String password) {
		this.endpoint = String.format("ws://%s/%s", endpoint, path);
		this.username = username;
		this.password = password;
	}

	public void addListener(T listener) {
		this.listeners.add(listener);
	}

	public void removeListener(T listener) {
		this.listeners.remove(listener);
	}

	public void connect() throws IOException, URISyntaxException, InterruptedException, DeploymentException {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, ClientEndpointConfig.Builder.create().configurator(new AuthorizationConfigurator(username, password)).build(), new URI(endpoint));
		latch.await();
	}

	public void disconnect() throws IOException, URISyntaxException, InterruptedException, DeploymentException {
		if (websocketSession.isOpen()) {
			websocketSession.close();
		}
	}

	public void onOpen(Session session, EndpointConfig config) {
		this.websocketSession = session;
		websocketSession.addMessageHandler(this);
		latch.countDown();
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		LOG.info("Closing a WebSocket due to {}", closeReason.getReasonPhrase());
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

	protected List<T> getListeners() {
		return listeners;
	}

	protected ObjectMapper getMapper() {
		return mapper;
	}
}
