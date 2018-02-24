package pl.pamsoft.imapcloud.integration;

import com.icegreen.greenmail.imap.commands.ImapCommand;
import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.integration.greenmail.CapacityToHighAppendCommand;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AccountControllerTestToHighCapacityIT extends AbstractGreenMailIntegrationTest {

	private AccountRestClient accountRestClient;

	@Autowired
	private AccountRepository accountRepository;

	@BeforeClass
	public void init() throws IOException, InterruptedException {
		super.init();
		accountRestClient = new AccountRestClient(getEndpoint(), getUsername(), getPassword());
	}

	@Override
	Class<? extends ImapCommand> getAppendClass() {
		return CapacityToHighAppendCommand.class;
	}


	@Test
	public void shouldTestCapacityStartingToLow() throws InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		accountRestClient.testCapacity(testAccountDto, response -> lock.countDown());
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
		Awaitility.await().atMost(2, TimeUnit.MINUTES).until(() -> null != accountRepository.getById(testAccountDto.getId()).getVerifiedAttachmentSizeBytes());
		assertEquals(accountRepository.getById(testAccountDto.getId()).getVerifiedAttachmentSizeBytes().intValue(), 659709);
	}
}
