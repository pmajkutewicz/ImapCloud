package pl.pamsoft.imapcloud.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class PerformanceDataClient {

	private static final Logger LOG = LoggerFactory.getLogger(PerformanceDataClient.class);
	ObjectMapper mapper = new ObjectMapper();
	private CountDownLatch latch = new CountDownLatch(1);
	private List<PerformanceDataEventListener> listeners = new ArrayList<>();
	private Session websocketSession;

	public void addListener(PerformanceDataEventListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(PerformanceDataEventListener listener) {
		this.listeners.remove(listener);
	}

	public void connect() throws IOException, URISyntaxException, InterruptedException, DeploymentException {
		String dest = "ws://localhost:9000/performance";
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, new URI(dest));
		getLatch().await();
	}

	public void disconnect() throws IOException, URISyntaxException, InterruptedException, DeploymentException {
		if (websocketSession.isOpen()) {
			websocketSession.close();
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Connected to server");
		this.websocketSession = session;
		latch.countDown();
	}

	@OnMessage
	public void onText(String message, Session session) {
		for (PerformanceDataEventListener listener : listeners) {
			try {
				PerformanceDataEvent eventData = mapper.readValue(message, PerformanceDataEvent.class);
				listener.onEventReceived(eventData);
			} catch (IOException e) {
				LOG.warn("Can't deserialize json", e);
			}
		}
		LOG.info("Message received from server: {}", message);
	}

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		LOG.info("Closing a WebSocket due to {}", reason.getReasonPhrase());
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
