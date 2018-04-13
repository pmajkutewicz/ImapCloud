package pl.pamsoft.imapcloud;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
@EnableGlobalMethodSecurity
public class ImapCloudApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ImapCloudApplication.class);
	}

	public static void main(String[] args) {
		//https://github.com/springfox/springfox/issues/2155
		final SpringApplication application = new SpringApplication(ImapCloudApplication.class);
		application.setBannerMode(Banner.Mode.CONSOLE);
		application.setWebApplicationType(WebApplicationType.SERVLET);
		application.run(args);
	}

}
