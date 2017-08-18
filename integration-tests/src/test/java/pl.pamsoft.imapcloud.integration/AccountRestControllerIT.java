package pl.pamsoft.imapcloud.integration;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
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

public class AccountRestControllerIT extends AbstractIntegrationTest {

	private AccountRestClient accountRestClient;

	@BeforeClass
	public void init() {
		accountRestClient = new AccountRestClient(getEndpoint(), "user", getPassword());
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
