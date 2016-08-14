package pl.pamsoft.imapcloud;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import static org.springframework.context.annotation.ComponentScan.Filter;

@ComponentScan(excludeFilters = { @Filter(type = FilterType.ANNOTATION, value = Configuration.class)})
@EnableAutoConfiguration
@EnableWebSocket
public class SpringTestConfig {
}
