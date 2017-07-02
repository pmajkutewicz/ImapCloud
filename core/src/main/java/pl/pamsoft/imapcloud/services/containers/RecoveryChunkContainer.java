package pl.pamsoft.imapcloud.services.containers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Map;

@Immutable
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public class RecoveryChunkContainer implements pl.pamsoft.imapcloud.api.containers.RecoveryChunkContainer {
	public static final RecoveryChunkContainer EMPTY = new RecoveryChunkContainer(null, null);

	private final String taskId;
	private final Account account;
	private final Map<String, File> fileMap;
	private final Map<String, List<FileChunk>> fileChunkMap;

	public RecoveryChunkContainer(String taskId, Account account) {
		this(taskId, account, null, null);
	}

	@JsonCreator
	private RecoveryChunkContainer(@JsonProperty("taskId") String taskId,
	                               @JsonProperty("account") Account account,
	                               @JsonProperty("fileMap") Map<String, File> fileMap,
	                               @JsonProperty("fileChunkMap") Map<String, List<FileChunk>> fileChunkMap) {
		this.taskId = taskId;
		this.account = account;
		this.fileMap = fileMap;
		this.fileChunkMap = fileChunkMap;
	}

	public static RecoveryChunkContainer addRecoveredFilesData(RecoveryChunkContainer rcc, Map<String, File> fileMap, Map<String, List<FileChunk>> fileChunkMap) {
		return new RecoveryChunkContainer(rcc.getTaskId(), rcc.getAccount(), fileMap, fileChunkMap);
	}

	@Override
	public String getTaskId() {
		return this.taskId;
	}

	@Override
	public Account getAccount() {
		return this.account;
	}

	public Map<String, File> getFileMap() {
		return this.fileMap;
	}

	public Map<String, List<FileChunk>> getFileChunkMap() {
		return this.fileChunkMap;
	}
}
