package pl.pamsoft.imapcloud.services.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractService<T> {

	@Autowired
	private ObjectMapper objectMapper;

	private List<WebSocketSession> activeSessions = new ArrayList<>();

	public void add(WebSocketSession session) {
		activeSessions.add(session);
	}

	public void remove(WebSocketSession session) {
		activeSessions.remove(session);
	}

	public void broadcast(String message) {
		for (WebSocketSession activeSession : activeSessions) {
			try {
				activeSession.sendMessage(new TextMessage(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void broadcast(T event) {
		for (WebSocketSession activeSession : activeSessions) {
			try {
				String json = objectMapper.writeValueAsString(event);
				activeSession.sendMessage(new TextMessage(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
