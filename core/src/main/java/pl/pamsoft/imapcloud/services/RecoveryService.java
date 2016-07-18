package pl.pamsoft.imapcloud.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.imap.ChunkRecovery;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.recovery.RecoveredFileChunksFileWriter;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

	@Value("${ic.recoveries.folder}")
	private String recoveriesFolder;

	public boolean recover(AccountDto selectedAccount) {
		final String taskId = UUID.randomUUID().toString();
		Future<?> task = getExecutor().submit(() -> {
			Thread.currentThread().setName("RecoveryTask-" + taskId);

			final Account account = accountRepository.getById(selectedAccount.getId());
			final GenericObjectPool<Store> poll = connectionPoolService.getOrCreatePoolForAccount(account);

			ChunkRecovery chunkRecovery = new ChunkRecovery(poll, statistics, performanceDataService);
			RecoveredFileChunksFileWriter recoveredFileChunksFileWriter = new RecoveredFileChunksFileWriter(filesIOService, recoveriesFolder);

			Stream.of(new RecoveryChunkContainer(taskId, account))
				.map(chunkRecovery)
				.map(recoveredFileChunksFileWriter)
				.forEach(rcc -> LOG.info("Done: {}", rcc.getTaskId()));

		});
		getTaskMap().put(taskId, task);
		return true;
	}

	public Map<String, byte[]> getResults() {
		try {
			Map<String, byte[]> results = new HashMap<>();
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(recoveriesFolder))) {
				for (Path entry : stream) {
					Path fileName = entry.getFileName();
					InputStream inputStream = filesIOService.getInputStream(entry.toFile());
					byte[] bytes = IOUtils.toByteArray(inputStream);
					results.put(fileName.toString(), Base64.getEncoder().encode(bytes));
				}
			}
			return results;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
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
