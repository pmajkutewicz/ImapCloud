package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.containers.VerifyChunkContainer;
import pl.pamsoft.imapcloud.services.verify.ChunkVerifierFacade;
import pl.pamsoft.imapcloud.services.verify.UpdateVerifyInfoInDb;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Function;

@Service
public class VerificationService extends AbstractBackgroundService {

	@Autowired
	private AccountServicesHolder accountServicesHolder;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	public boolean validate(List<FileChunk> fileChunks) {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> task = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("VT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			final Account account = accountRepository.getById(fileChunks.get(0).getOwnerFile().getOwnerAccount().getId());
			AccountService accountService = accountServicesHolder.getAccountService(account.getType());

			Function<FileChunk, VerifyChunkContainer> packItInContainer = chunk ->
				new VerifyChunkContainer(taskId, chunk.getFileChunkUniqueId(), chunk.getOwnerFile().getFileHash(), chunk.getId(), chunk.getMessageId());
			ChunkVerifierFacade chunkVerifier = new ChunkVerifierFacade(accountService.getChunkVerifier(account), getMonitoringHelper());
			Function<VerifyChunkContainer, VerifyChunkContainer> chunkInfoUpdater = new UpdateVerifyInfoInDb(fileChunkRepository);

			fileChunks.stream()
				.map(packItInContainer)
				.map(chunkVerifier)
				.map(chunkInfoUpdater)
				.forEach(System.out::println);
		});
		getTaskMap().put(taskId, task);
		return true;
	}

	@Override
	protected int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	protected String getNameFormat() {
		return "VerificationTask-%d";
	}

}
