package pl.pamsoft.imapcloud.integration;

import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class Common {

	private final String responseNotReceivedMsg;
	private final int testTimeout;
	private final AccountRestClient accountRestClient;

	public Common(AccountRestClient accountRestClient, String responseNotReceivedMsg, int testTimeout) {
		this.responseNotReceivedMsg = responseNotReceivedMsg;
		this.testTimeout = testTimeout;
		this.accountRestClient = accountRestClient;
	}

	public AccountDto shouldCreateAccount(String username, String password, String cryptoKey, String expectedAccountEmail) throws IOException, InterruptedException {
		// get available accounts
		CountDownLatch lock = new CountDownLatch(1);
		List<AccountInfo> responses = new ArrayList<>();
		accountRestClient.getAvailableAccounts(accountProviders -> {
				responses.addAll(accountProviders.getAccountProviders());
				lock.countDown();
			}
		);
		assertTrue(lock.await(testTimeout, TimeUnit.MILLISECONDS), responseNotReceivedMsg);

		Optional<AccountInfo> accountInfo = responses.stream().filter(a -> "vfs".equals(a.getType())).filter(a -> "tmp".equals(a.getProperty("fs"))).findFirst();
		if (!accountInfo.isPresent()) {
			fail("No VFS account available");
		}

		//create account
		CountDownLatch lock2 = new CountDownLatch(1);
		accountRestClient.createAccount(accountInfo.get(), username, password, cryptoKey, callback -> lock2.countDown());
		assertTrue(lock2.await(testTimeout, TimeUnit.MILLISECONDS), responseNotReceivedMsg);

		//verify created Account
		List<AccountDto> result = new ArrayList<>();
		CountDownLatch lock3 = new CountDownLatch(1);
		accountRestClient.listAccounts(response -> {
			Optional<AccountDto> createdAccount = response.getAccount().stream().filter(a -> expectedAccountEmail.equals(a.getEmail())).findFirst();
			assertTrue(createdAccount.isPresent());
			result.add(createdAccount.get());
			lock3.countDown();
		});
		assertTrue(lock3.await(testTimeout, TimeUnit.MILLISECONDS), responseNotReceivedMsg);

		return result.get(0);
	}
}
