package pl.pamsoft.imapcloud.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import pl.pamsoft.imapcloud.ImapCloudApplication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ImapCloudApplication.class})
@DirtiesContext
public abstract class AbstractIntegrationTest extends AbstractTestNGSpringContextTests {

	static final String RESPONSE_NOT_RECEIVED = "Response not received.";
	static final int TEST_TIMEOUT = 2000;

	@Value("${security.user.password}")
	private String password;

	@Value("${local.server.port}")
	private int targetWebServerPort;

	String getEndpoint() {
		return "127.0.0.1:" + targetWebServerPort;
	}

	String getPassword() {
		return password;
	}
}
