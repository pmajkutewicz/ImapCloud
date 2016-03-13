package pl.pamsoft.imapcloud;


import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringApplicationConfiguration(ImapCloudApplication.class)
@WebIntegrationTest(randomPort = true)
@DirtiesContext
public class ImapCloudApplicationTests extends AbstractTestNGSpringContextTests {

	@Test
	public void contextLoads() {
	}
}
