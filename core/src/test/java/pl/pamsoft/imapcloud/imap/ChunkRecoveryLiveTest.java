package pl.pamsoft.imapcloud.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.ConnectionPoolService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;

import static org.mockito.Mockito.mock;

public class ChunkRecoveryLiveTest {

	private ChunkRecovery chunkRecovery;

	private PerformanceDataService performanceDataService = mock(PerformanceDataService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	public void init() {
		Account a = new Account();
		a.setLogin("adam.b92");
		a.setPassword("&*PJMsSyhshwfKmmEg*3xY$Vgca8z5#c");
		a.setImapServerAddress("imap.mail.yahoo.com");
		a.setMaxConcurrentConnections(5);
		GenericObjectPool<Store> pool = new ConnectionPoolService().getOrCreatePoolForAccount(a);
		chunkRecovery = new ChunkRecovery(pool, performanceDataService, monitoringHelper);
	}

	public void testLive() {
		chunkRecovery.apply(RecoveryChunkContainer.EMPTY);
	}
}
