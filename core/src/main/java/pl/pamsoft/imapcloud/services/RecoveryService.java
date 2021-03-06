package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.RecoveredFileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.services.containers.RecoveryChunkContainer;
import pl.pamsoft.imapcloud.services.recovery.ChunkRecovererFacade;
import pl.pamsoft.imapcloud.services.recovery.FileRecovery;
import pl.pamsoft.imapcloud.services.recovery.RCCtoRecoveredFileDtoConverter;
import pl.pamsoft.imapcloud.services.recovery.RecoveredFileChunksFileReader;
import pl.pamsoft.imapcloud.services.recovery.RecoveredFileChunksFileWriter;

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

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountServicesHolder accountServicesHolder;

	private String recoveriesFolder;

	public boolean recover(AccountDto selectedAccount) {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> task = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("RT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));

			final Account account = accountRepository.getById(selectedAccount.getId());
			AccountService accountService = accountServicesHolder.getAccountService(account.getType());

			ChunkRecovererFacade chunkRecovererFacade = new ChunkRecovererFacade(accountService.getChunkRecoverer(account), getMonitoringHelper());
			RecoveredFileChunksFileWriter recoveredFileChunksFileWriter = new RecoveredFileChunksFileWriter(filesIOService, recoveriesFolder);

			Stream.of(new RecoveryChunkContainer(taskId, account))
				.map(chunkRecovererFacade)
				.map(recoveredFileChunksFileWriter)
				.forEach(System.out::println);

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
					.forEach(i -> results.put(i.getKey(), i.getValue()));
				return results;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}

	public boolean recoverFiles(String taskId, Set<String> uniqueFilesIds) {
		String fileName = String.format("%s.%s", taskId, "ic");
		Path path = Paths.get(recoveriesFolder, fileName + ".zip");

		Function<Path, RecoveryChunkContainer> reader = new RecoveredFileChunksFileReader(filesIOService);
		Function<RecoveryChunkContainer, RecoveryChunkContainer> fileRecovery =
			new FileRecovery(uniqueFilesIds, fileRepository, fileChunkRepository, accountRepository, cryptoService);

		Stream.of(path)
			.map(reader)
			.map(fileRecovery)
			.forEach(System.out::println);

		return true;
	}

	@Override
	protected int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	protected String getNameFormat() {
		return "RecoveryTask-%d";
	}

	@Value("${ic.recoveries.folder}")
	public void setRecoveriesFolder(String recoveriesFolder) {
		this.recoveriesFolder = recoveriesFolder;
	}

}
