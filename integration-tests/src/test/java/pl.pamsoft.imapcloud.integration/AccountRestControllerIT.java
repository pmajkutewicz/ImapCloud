package pl.pamsoft.imapcloud.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.ImapCloudApplication;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ImapCloudApplication.class})
@DirtiesContext
public class AccountRestControllerIT extends AbstractTestNGSpringContextTests {

	private static final String RESPONSE_NOT_RECEIVED = "Response not received.";
	private static final int TEST_TIMEOUT = 2000;

	@Value("${security.user.password}")
	private String password;

	@Value("${local.server.port}")
	private int targetWebServerPort;

	private AccountRestClient accountRestClient;

	@BeforeClass
	public void init() {
		accountRestClient = new AccountRestClient("127.0.0.1:" + targetWebServerPort, "user", password);
	}

	@Test
	public void shouldContainVFSAccount() throws IOException, InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		accountRestClient.getAvailableAccounts(accountProviders -> {
			assertThat(accountProviders.getAccountProviders().size(), greaterThanOrEqualTo(1));
			assertThat(accountProviders.getAccountProviders().stream().map(AccountInfo::getType).collect(toSet()), hasItem("vfs"));
			lock.countDown();
			}
		);
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);

	}

}
