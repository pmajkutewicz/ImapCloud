package pl.pamsoft.imapcloud.services.websocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PerformanceDataService {

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
}
