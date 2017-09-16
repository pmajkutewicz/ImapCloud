package pl.pamsoft.imapcloud.integration;

import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class AccountRestControllerIT extends AbstractIntegrationTest {

	private AccountRestClient accountRestClient;
	private Common common;

	@BeforeClass
	public void init() {
		accountRestClient = new AccountRestClient(getEndpoint(), "user", getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
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
		String username = RandomStringUtils.randomAlphabetic(10);
		String expectedAccountEmail = String.format("%s@localhost_tmp", username);
		AccountDto accountDto = common.shouldCreateAccount(username, "test", "key", expectedAccountEmail);
		assertNotNull(accountDto);
		assertEquals(accountDto.getEmail(), expectedAccountEmail);
	}
}
