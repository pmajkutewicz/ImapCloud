package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.containers.DeleteChunkContainer;
import pl.pamsoft.imapcloud.services.delete.ChunkDeleterFacade;
import pl.pamsoft.imapcloud.services.delete.DeleteFileChunkFromDb;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class DeletionService extends AbstractBackgroundService {

	@Autowired
	private AccountServicesHolder accountServicesHolder;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	//FIXME: Delete by chunks not by whole file
	public boolean delete(File fileToDelete) {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> task = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("DelT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			final Account account = accountRepository.getById(fileToDelete.getOwnerAccount().getId());
			AccountService accountService = accountServicesHolder.getAccountService(account.getType());

			Function<File, DeleteChunkContainer> packItInContainer = file -> new DeleteChunkContainer(taskId, file.getFileUniqueId(), file.getFileHash());
			ChunkDeleterFacade chunkDeleter = new ChunkDeleterFacade(accountService.getChunkDeleter(account), getMonitoringHelper());
			DeleteFileChunkFromDb deleteFileChunkFromDb = new DeleteFileChunkFromDb(fileChunkRepository);

			Stream.of(fileToDelete)
				.map(packItInContainer)
				.map(chunkDeleter)
				.map(deleteFileChunkFromDb)
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
		return "DeletionTask-%d";
	}

}
