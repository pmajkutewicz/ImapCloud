package pl.pamsoft.imapcloud.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.ImapCloudApplication;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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

	@Test(dependsOnMethods = "shouldContainVFSAccount")
	public void shouldCreateAccount() throws IOException, InterruptedException {
		// get available accounts
		CountDownLatch lock = new CountDownLatch(1);
		List<AccountInfo> responses = new ArrayList<>();
		accountRestClient.getAvailableAccounts(accountProviders -> {
				responses.addAll(accountProviders.getAccountProviders());
				lock.countDown();
			}
		);
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);

		Optional<AccountInfo> accountInfo = responses.stream().filter(a -> "vfs".equals(a.getType())).filter(a -> "tmp".equals(a.getProperty("fs"))).findFirst();
		if (!accountInfo.isPresent()) {
			fail("No VFS account available");
		}

		//create account
		CountDownLatch lock2 = new CountDownLatch(1);
		accountRestClient.createAccount(accountInfo.get(), "test1415261", "test", "key", callback -> lock2.countDown());
		assertTrue(lock2.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);

		//verify created Account
		CountDownLatch lock3 = new CountDownLatch(1);
		accountRestClient.listAccounts(response -> {
			Optional<AccountDto> createdAccount = response.getAccount().stream().filter(a -> "test1415261@localhost_tmp".equals(a.getEmail())).findFirst();
			assertTrue(createdAccount.isPresent());
			lock3.countDown();
		});
		assertTrue(lock3.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
	}
}
