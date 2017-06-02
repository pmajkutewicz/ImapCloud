package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.imap.ChunkVerifier;

import javax.mail.Store;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
public class VerificationService extends AbstractBackgroundService {

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	public boolean validate(List<FileChunk> fileChunks) {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> task = runAsyncOnExecutor(() -> {
			Thread.currentThread().setName("VT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			fileChunks
				.forEach(chunk -> {
						GenericObjectPool<Store> connectionPool = connectionPoolService.getOrCreatePoolForAccount(chunk.getOwnerFile().getOwnerAccount());
						ChunkVerifier chunkVerifier = new ChunkVerifier(connectionPool, getMonitoringHelper());
						Boolean chunkExists = chunkVerifier.apply(chunk);
						fileChunkRepository.markChunkVerified(chunk.getId(), chunkExists);
					}
				);
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
