package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.RecoveredFileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.imap.ChunkRecovery;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.recovery.RCCtoRecoveredFileDtoConverter;
import pl.pamsoft.imapcloud.services.recovery.RecoveredFileChunksFileReader;
import pl.pamsoft.imapcloud.services.recovery.RecoveredFileChunksFileWriter;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class RecoveryService extends AbstractBackgroundService {

	private static final Logger LOG = LoggerFactory.getLogger(RecoveryService.class);

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private Statistics statistics;

	@Autowired
	private PerformanceDataService performanceDataService;

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

	public Map<String, List<RecoveredFileDto>> getResults() {
		try {
			Function<Path, RecoveryChunkContainer> reader = new RecoveredFileChunksFileReader(filesIOService);
			Function<RecoveryChunkContainer, List<RecoveredFileDto>> converter = new RCCtoRecoveredFileDtoConverter(cryptoService);

			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(recoveriesFolder))) {
				Map<String, List<RecoveredFileDto>> results = new HashMap<>();
				StreamSupport.stream(dirStream.spliterator(), false)
					.map(reader)
					.map(i -> new SimpleEntry<String, List<RecoveredFileDto>>(i.getTaskId(), converter.apply(i)))
					.forEach(i-> results.put(i.getKey(), i.getValue()));
				return results;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}

	public boolean recoverFiles(Set<String> uniqueFilesIds) {
		return false;  //TODO: Not implemented yet. To change body of created methods use File | Settings | File Templates.
	}

	@Override
	int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	String getNameFormat() {
		return "RecoveryTask-%d";
	}

	@Value("${ic.recoveries.folder}")
	public void setRecoveriesFolder(String recoveriesFolder) {
		this.recoveriesFolder = recoveriesFolder;
	}
}
