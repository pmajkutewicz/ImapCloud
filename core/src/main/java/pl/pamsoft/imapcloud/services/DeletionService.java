package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.imap.FileDeleter;

import javax.mail.Store;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
public class DeletionService extends AbstractBackgroundService {

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	public boolean delete(File fileToDelete) {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> task = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("DelT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			GenericObjectPool<Store> connectionPool = connectionPoolService.getOrCreatePoolForAccount(fileToDelete.getOwnerAccount());
			FileDeleter fileDeleter = new FileDeleter(connectionPool, getMonitoringHelper());
			Boolean isDeletedSuccessfully = fileDeleter.apply(fileToDelete);
			if (isDeletedSuccessfully) {
				fileChunkRepository.deleteFileChunks(fileToDelete.getFileUniqueId());
			}
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
