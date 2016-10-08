package pl.pamsoft.imapcloud.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;

public class TaskProgressClient extends AbstractWebsocketClient<TaskProgressEventListener> implements MessageHandler.Whole<String> {

	private static final Logger LOG = LoggerFactory.getLogger(TaskProgressClient.class);

	public TaskProgressClient(String endpoint, String username, String password) {
		super(endpoint, "tasks", username, password);
	}

	public void onOpen(Session session, EndpointConfig config) {
		LOG.debug("Connected to task progress server");
		super.onOpen(session, config);
	}

	public void onMessage(String message) {
		for (TaskProgressEventListener listener : getListeners()) {
			try {
				TaskProgressEvent eventData = getMapper().readValue(message, TaskProgressEvent.class);
				listener.onEventReceived(eventData);
			} catch (IOException e) {
				LOG.warn("Can't deserialize json", e);
			}
		}
		LOG.info("Message received from server: {}", message);
	}
}
