package pl.pamsoft.imapcloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pl.pamsoft.imapcloud.websocket.DefaultEchoService;
import pl.pamsoft.imapcloud.websocket.EchoService;
import pl.pamsoft.imapcloud.websocket.EchoWebSocketHandler;
import pl.pamsoft.imapcloud.websocket.PerformanceDataSocketHandler;
import pl.pamsoft.imapcloud.websocket.TasksProgressSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
	private PerformanceDataSocketHandler performanceDataSocketHandler;

	@Autowired
	private TasksProgressSocketHandler tasksProgressSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(echoWebSocketHandler(), "/echo");
		registry.addHandler(performanceDataSocketHandler, "/performance").setAllowedOrigins("*");
		registry.addHandler(tasksProgressSocketHandler, "/tasks").setAllowedOrigins("*");
	}

	@Bean
	public WebSocketHandler echoWebSocketHandler() {
		return new EchoWebSocketHandler(echoService());
	}

	@Bean
	public EchoService echoService() {
		return new DefaultEchoService("Did you say \"%s\"?");
	}
}
