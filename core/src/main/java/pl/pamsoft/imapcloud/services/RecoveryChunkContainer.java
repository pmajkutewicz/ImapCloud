package pl.pamsoft.imapcloud.services;

import lombok.Getter;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Map;

@Immutable
@Getter
public class RecoveryChunkContainer {
	public static final RecoveryChunkContainer EMPTY = new RecoveryChunkContainer(null, null);

	private final String taskId;
	private final Account account;
	private final Map<String, File> fileMap;
	private final Map<String, List<FileChunk>> fileChunkMap;

	 public RecoveryChunkContainer(String taskId, Account account) {
		this(taskId, account, null, null);
	}

	private RecoveryChunkContainer(String taskId, Account account, Map<String, File> fileMap, Map<String, List<FileChunk>> fileChunkMap) {
		this.taskId = taskId;
		this.account = account;
		this.fileMap = fileMap;
		this.fileChunkMap = fileChunkMap;
	}

	public static RecoveryChunkContainer addRecoveredFilesData(RecoveryChunkContainer rcc, Map<String, File> fileMap, Map<String, List<FileChunk>> fileChunkMap) {
		return new RecoveryChunkContainer(rcc.getTaskId(), rcc.getAccount(), fileMap, fileChunkMap);
	}
}
