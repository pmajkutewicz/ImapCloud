package pl.pamsoft.imapcloud.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.ConnectionPoolService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;

import static org.mockito.Mockito.mock;

public class ChunkRecoveryLiveTest {

	private ChunkRecovery chunkRecovery;
	private GenericObjectPool<Store> pool;

	private Statistics statistics = mock(Statistics.class);
	private PerformanceDataService performanceDataService = mock(PerformanceDataService.class);


	public void setup() {
		Account a = new Account();
		a.setLogin("adam.b92");
		a.setPassword("&*PJMsSyhshwfKmmEg*3xY$Vgca8z5#c");
		a.setImapServerAddress("imap.mail.yahoo.com");
		a.setMaxConcurrentConnections(5);
		pool = new ConnectionPoolService().getOrCreatePoolForAccount(a);
		chunkRecovery = new ChunkRecovery(pool, statistics, performanceDataService);
	}

	public void testLive() {
		RecoveryChunkContainer result = chunkRecovery.apply(RecoveryChunkContainer.EMPTY);
	}
}
