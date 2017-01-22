package pl.pamsoft.imapcloud.config.ff4j;

import org.ff4j.FF4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FF4JConfig {

	@Bean
	public FF4j createFF() {
		return new FF4j("ff4j.xml");
	}
}
