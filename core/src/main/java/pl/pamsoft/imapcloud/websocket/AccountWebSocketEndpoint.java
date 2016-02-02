package pl.pamsoft.imapcloud.websocket;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/account")
public class AccountWebSocketEndpoint {

	@OnMessage
	public void handleMessage(Session session, String message) throws IOException {
		session.getBasicRemote()
			.sendText("Reversed: " + new StringBuilder(message).reverse());
	}
}
