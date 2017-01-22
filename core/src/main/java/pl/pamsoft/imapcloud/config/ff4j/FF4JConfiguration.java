package pl.pamsoft.imapcloud.config.ff4j;

import org.ff4j.FF4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({FF4j.class})
public class FF4JConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public FF4j getFF4j() {
		return new FF4j();
	}
}
