package pl.pamsoft.imapcloud.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

@Component
public class PerformanceDataSocketHandler extends TextWebSocketHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EchoWebSocketHandler.class);

	@Autowired
	private PerformanceDataService performanceDataService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		performanceDataService.add(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		performanceDataService.remove(session);
		super.afterConnectionClosed(session, status);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		LOG.debug(message.getPayload());
		session.sendMessage(new TextMessage("Sukcess"));
	}
}
