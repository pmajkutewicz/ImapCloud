package pl.pamsoft.imapcloud.websocket;

import java.net.ConnectException;

public interface ServerConnection {
	AccountClient getAccountSession();
	void start() throws ConnectException;
	void close();
}
