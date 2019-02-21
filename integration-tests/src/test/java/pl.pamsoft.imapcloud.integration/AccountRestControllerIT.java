package pl.pamsoft.imapcloud.integration;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountRestControllerIT extends AbstractIntegrationTest {

	private AccountRestClient accountRestClient;
	private Common common;

	@BeforeAll
	void init() {
		accountRestClient = new AccountRestClient(getEndpoint(), getUsername(), getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
	}

	@Test
	@Order(1)
	void shouldContainVFSAccount() throws IOException, InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		accountRestClient.getAvailableAccounts(accountProviders -> {
			assertThat(accountProviders.getAccountProviders().size(), greaterThanOrEqualTo(1));
			assertThat(accountProviders.getAccountProviders().stream().map(AccountInfo::getType).collect(toSet()), hasItem("vfs"));
			lock.countDown();
			}
		);
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
	}

	@Test
	@Order(2)
	void shouldCreateAccount() throws IOException, InterruptedException {
		String username = RandomStringUtils.randomAlphabetic(10);
		String expectedAccountEmail = String.format("%s@localhost_tmp", username);
		AccountDto accountDto = common.shouldCreateAccount(username, "test", "key", expectedAccountEmail);
		assertNotNull(accountDto);
		assertEquals(expectedAccountEmail, accountDto.getEmail());
	}
}
