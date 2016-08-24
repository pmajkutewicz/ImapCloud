package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.imap.FileDeleter;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
public class DeletionService extends AbstractBackgroundService {

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	@Autowired
	private PerformanceDataService performanceDataService;

	@Autowired
	private MonitoringHelper monitoringHelper;

	public boolean delete(File fileToDelete) {
		final String taskId = UUID.randomUUID().toString();
		Future<?> task = getExecutor().submit(() -> {
			Thread.currentThread().setName("DeletionTask-" + taskId);
			GenericObjectPool<Store> connectionPool = connectionPoolService.getOrCreatePoolForAccount(fileToDelete.getOwnerAccount());
			FileDeleter fileDeleter = new FileDeleter(connectionPool, performanceDataService, monitoringHelper);
			Boolean isDeletedSuccessfully = fileDeleter.apply(fileToDelete);
			if (isDeletedSuccessfully) {
				fileChunkRepository.deleteFileChunks(fileToDelete.getFileUniqueId());
			}
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
		return "DeletionTask-%d";
	}

	@Override
	MonitoringHelper getMonitoringHelper() {
		return null;
	}
}
