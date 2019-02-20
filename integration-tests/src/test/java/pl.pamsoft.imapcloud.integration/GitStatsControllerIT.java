package pl.pamsoft.imapcloud.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.rest.GitStatusRestClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitStatsControllerIT extends AbstractIntegrationTest {

	private GitStatusRestClient gitStatusRestClient;

	@BeforeAll
	public void init() {
		gitStatusRestClient = new GitStatusRestClient(getEndpoint(), getUsername(), getPassword());
	}

	@Test
	void shouldReturnGitStats() throws IOException, InterruptedException {
		CountDownLatch lock = new CountDownLatch(1);
		gitStatusRestClient.getGitStatus(stats -> {
				assertNotNull(stats.getCommitId());
				assertNotNull(stats.getBuildVersion());
				lock.countDown();
			}
		);
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
	}

}
