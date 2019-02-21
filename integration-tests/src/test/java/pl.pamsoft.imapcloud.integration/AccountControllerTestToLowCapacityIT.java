package pl.pamsoft.imapcloud.integration;

import com.icegreen.greenmail.imap.commands.ImapCommand;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.integration.greenmail.CapacityToLowAppendCommand;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
class AccountControllerTestToLowCapacityIT extends AbstractGreenMailIntegrationTest {

	private AccountRestClient accountRestClient;

	@Autowired
	private AccountRepository accountRepository;

	@BeforeAll
	void init() throws IOException, InterruptedException {
		super.init();
		accountRestClient = new AccountRestClient(getEndpoint(), getUsername(), getPassword());
	}

	@Override
	Class<? extends ImapCommand> getAppendClass() {
		return CapacityToLowAppendCommand.class;
	}


	@Test
	void shouldTestCapacityStartingToLow() throws IOException, InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		accountRestClient.testCapacity(testAccountDto, response -> lock.countDown());
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
		Awaitility.await().atMost(2, TimeUnit.MINUTES).until(() -> null != accountRepository.getById(testAccountDto.getId()).getVerifiedAttachmentSizeBytes());
		Account byId = accountRepository.getById(testAccountDto.getId());
		assertNotNull(byId.getVerifiedAttachmentSizeBytes());
		assertTrue(7662360 == byId.getVerifiedAttachmentSizeBytes() || 7662357 == byId.getVerifiedAttachmentSizeBytes());
	}
}
