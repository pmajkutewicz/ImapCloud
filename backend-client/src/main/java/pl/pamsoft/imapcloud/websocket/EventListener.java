package pl.pamsoft.imapcloud.websocket;

public interface EventListener<T extends Event> {
	void onEventReceived(T event);
}
