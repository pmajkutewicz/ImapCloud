package pl.pamsoft.imapcloud.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.ConnectionPoolService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;
import pl.pamsoft.imapcloud.services.common.TasksProgressService;

import javax.mail.Store;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class ChunkRecoveryLiveTest {

	private ChunkRecovery chunkRecovery;

	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@SuppressWarnings("unchecked")
	public void init() {
		Account a = new Account();
		a.setLogin("adam.b92");
		a.setPassword("&*PJMsSyhshwfKmmEg*3xY$Vgca8z5#c");
		a.setImapServerAddress("imap.mail.yahoo.com");
		a.setMaxConcurrentConnections(5);
		GenericObjectPool<Store> pool = new ConnectionPoolService().getOrCreatePoolForAccount(a);
		TasksProgressService tasksProgressService = Mockito.mock(TasksProgressService.class);
		Map<String, TaskProgress> taskProgressMap = Mockito.mock(Map.class);
		chunkRecovery = new ChunkRecovery(pool, monitoringHelper, tasksProgressService, taskProgressMap);
	}

	public void testLive() {
		chunkRecovery.apply(RecoveryChunkContainer.EMPTY);
	}
}
