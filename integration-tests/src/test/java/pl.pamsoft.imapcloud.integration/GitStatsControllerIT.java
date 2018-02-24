package pl.pamsoft.imapcloud.integration;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.rest.GitStatusRestClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class GitStatsControllerIT extends AbstractIntegrationTest {

	private GitStatusRestClient gitStatusRestClient;

	@BeforeClass
	public void init() {
		gitStatusRestClient = new GitStatusRestClient(getEndpoint(), getUsername(), getPassword());
	}

	@Test
	public void shouldReturnGitStats() throws IOException, InterruptedException {
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
