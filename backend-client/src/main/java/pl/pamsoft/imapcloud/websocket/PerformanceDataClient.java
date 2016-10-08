package pl.pamsoft.imapcloud.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;

public class PerformanceDataClient extends AbstractWebsocketClient<PerformanceDataEventListener> implements MessageHandler.Whole<String> {

	private static final Logger LOG = LoggerFactory.getLogger(PerformanceDataClient.class);

	public PerformanceDataClient(String endpoint, String username, String password) {
		super(endpoint, "performance", username, password);
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("Connected to performance data server");
		super.onOpen(session, config);
	}

	@Override
	public void onMessage(String message) {
		for (PerformanceDataEventListener listener : getListeners()) {
			try {
				PerformanceDataEvent eventData = getMapper().readValue(message, PerformanceDataEvent.class);
				listener.onEventReceived(eventData);
			} catch (IOException e) {
				LOG.warn("Can't deserialize json", e);
			}
		}
		LOG.info("Message received from server: {}", message);
	}
}
