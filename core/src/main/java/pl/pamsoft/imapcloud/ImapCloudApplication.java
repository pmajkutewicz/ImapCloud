package pl.pamsoft.imapcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import pl.pamsoft.imapcloud.websocket.DefaultEchoService;
import pl.pamsoft.imapcloud.websocket.EchoService;
import pl.pamsoft.imapcloud.websocket.EchoWebSocketHandler;
import pl.pamsoft.imapcloud.websocket.ReverseWebSocketEndpoint;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableWebSocket
public class ImapCloudApplication extends SpringBootServletInitializer implements WebSocketConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(ImapCloudApplication.class);

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(echoWebSocketHandler(), "/echo").withSockJS();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ImapCloudApplication.class);
	}

	@Bean
	public EchoService echoService() {
		return new DefaultEchoService("Did you say \"%s\"?");
	}

	@Bean
	public WebSocketHandler echoWebSocketHandler() {
		return new EchoWebSocketHandler(echoService());
	}

	@Bean
	public ReverseWebSocketEndpoint reverseWebSocketEndpoint() {
		return new ReverseWebSocketEndpoint();
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ImapCloudApplication.class, args);
	}

}
