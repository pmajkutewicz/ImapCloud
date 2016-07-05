package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.imap.ChunkRecovery;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.recovery.RecoveredFileChunksFileWriter;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Service
public class RecoveryService extends AbstractBackgroundService {

	private static final Logger LOG = LoggerFactory.getLogger(RecoveryService.class);

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private Statistics statistics;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private PerformanceDataService performanceDataService;

	public boolean recover(AccountDto selectedAccount) {
		final String taskId = UUID.randomUUID().toString();
		Future<?> task = getExecutor().submit(() -> {
			Thread.currentThread().setName("RecoveryTask-" + taskId);

			final Account account = accountRepository.getById(selectedAccount.getId());
			final GenericObjectPool<Store> poll = connectionPoolService.getOrCreatePoolForAccount(account);

			ChunkRecovery chunkRecovery = new ChunkRecovery(poll, statistics, performanceDataService);
			RecoveredFileChunksFileWriter recoveredFileChunksFileWriter = new RecoveredFileChunksFileWriter(filesIOService);

			Stream.of(new RecoveryChunkContainer(taskId, account))
				.map(chunkRecovery)
				.map(recoveredFileChunksFileWriter)
				.forEach(rcc -> LOG.info("Done: {}", rcc.getTaskId()));

		});
		getTaskMap().put(taskId, task);
		return true;
	}

	@Override
	int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	String getNameFormat() {
		return "RecoveryTask-%d";
	}
}
