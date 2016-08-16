package pl.pamsoft.imapcloud.config.jmx;


import com.jamonapi.jmx.JmxUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class Jamon {

	@PostConstruct
	public void setup() {
		JmxUtils.registerMbeans();
	}
}
