package pl.pamsoft.imapcloud.config.jmx;

import com.jamonapi.jmx.JmxUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class Jamon {

	@PostConstruct
	public void init() {
		JmxUtils.registerMbeans();
	}

	@PreDestroy
	public void destroy() {
		JmxUtils.unregisterMbeans();
	}
}
