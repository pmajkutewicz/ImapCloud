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
import pl.pamsoft.imapcloud.integration.greenmail.CapacityToHighAppendCommand;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
class AccountControllerTestToHighCapacityIT extends AbstractGreenMailIntegrationTest {

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
		return CapacityToHighAppendCommand.class;
	}


	@Test
	void shouldTestCapacityStartingToLow() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		accountRestClient.testCapacity(testAccountDto, response -> lock.countDown());
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
		Awaitility.await().atMost(2, TimeUnit.MINUTES).until(() -> null != accountRepository.getById(testAccountDto.getId()).getVerifiedAttachmentSizeBytes());
		assertEquals(659709, accountRepository.getById(testAccountDto.getId()).getVerifiedAttachmentSizeBytes().intValue());
	}
}
