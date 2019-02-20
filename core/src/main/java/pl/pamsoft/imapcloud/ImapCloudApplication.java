package pl.pamsoft.imapcloud;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableScheduling
@EnableGlobalMethodSecurity(securedEnabled = true)
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
