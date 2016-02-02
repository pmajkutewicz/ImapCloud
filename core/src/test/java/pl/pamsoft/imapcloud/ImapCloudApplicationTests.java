package pl.pamsoft.imapcloud;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ImapCloudApplication.class)
@WebIntegrationTest(randomPort = true)
@DirtiesContext
public class ImapCloudApplicationTests {

	@Test
	public void contextLoads() {
	}
}
