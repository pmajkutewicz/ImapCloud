package pl.pamsoft.imapcloud.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.pamsoft.imapcloud.services.websocket.TasksProgressEventService;

@Component
public class TasksProgressSocketHandler extends TextWebSocketHandler {

	private static final Logger LOG = LoggerFactory.getLogger(TasksProgressSocketHandler.class);

	@Autowired
	private TasksProgressEventService tasksProgressEventService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		tasksProgressEventService.add(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		tasksProgressEventService.remove(session);
		super.afterConnectionClosed(session, status);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		LOG.debug(message.getPayload());
	}
}
