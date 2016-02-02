package pl.pamsoft.imapcloud.websocket;

@FunctionalInterface
public interface OnFailedInitNotification {
	void initializationFailedNotification(Exception e);
}
