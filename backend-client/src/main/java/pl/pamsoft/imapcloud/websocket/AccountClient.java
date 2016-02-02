package pl.pamsoft.imapcloud.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class AccountClient {

	private static final Logger LOG = LoggerFactory.getLogger(AccountClient.class);

	private CountDownLatch latch = new CountDownLatch(1);
	private Session websocketSession;

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Connected to server");
		this.websocketSession = session;
		latch.countDown();
	}

	@OnMessage
	public void onText(String message, Session session) {
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
