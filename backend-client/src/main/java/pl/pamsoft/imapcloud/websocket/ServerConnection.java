package pl.pamsoft.imapcloud.websocket;

import java.net.ConnectException;

public interface ServerConnection {
	PerformanceDataClient getPerformanceDataClient();
	void start() throws ConnectException;
	void close();
}
