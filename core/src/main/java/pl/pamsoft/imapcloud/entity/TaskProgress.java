package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.progress.ProgressStatus;
import pl.pamsoft.imapcloud.websocket.TaskType;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgress {
	@Id
	private String id;
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	@OneToMany(fetch = FetchType.EAGER)
	private Map<String, EntryProgress> progressMap;

	public void process(String currentFileAbsolutePath, long cumulativeFileProgress) {
		EntryProgress entryProgress = progressMap.get(currentFileAbsolutePath);
		entryProgress.setProgress(cumulativeFileProgress);
		if (cumulativeFileProgress == entryProgress.getSize()){
			entryProgress.setStatus(ProgressStatus.UPLOADED);
		} else {
			entryProgress.setStatus(ProgressStatus.UPLOADING);
		}
	}

	public void processFolder(String folder) {
		bytesOverall++;
		EntryProgress entryProgress = progressMap.get(folder);
		entryProgress.setProgress(entryProgress.getSize());
		entryProgress.setStatus(ProgressStatus.RECOVERY_SCANNED);
	}

	public void markFileAlreadyUploaded(String currentFileAbsolutePath, long fileSize) {
		EntryProgress entryProgress = progressMap.get(currentFileAbsolutePath);
		entryProgress.setProgress(fileSize);
		entryProgress.setStatus(ProgressStatus.ALREADY_UPLOADED);
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TaskType getType() {
		return this.type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public long getBytesOverall() {
		return this.bytesOverall;
	}

	public void setBytesOverall(long bytesOverall) {
		this.bytesOverall = bytesOverall;
	}

	/**
	 * For recovery task:
	 * - return nb. of completed folders (scanned)
	 * For other tasks:
	 * - returns sum of processed bytes
	 * @return
	 */
	public long getBytesProcessed() {
		if (TaskType.RECOVERY == type) {
			return progressMap.values().stream().mapToLong(e -> e.getStatus().isTaskCompleted() ? 1 : 0).sum();
		} else {
			return progressMap.values().stream().mapToLong(EntryProgress::getProgress).sum();
		}
	}

	public Map<String, EntryProgress> getProgressMap() {
		return this.progressMap;
	}

	public void setProgressMap(Map<String, EntryProgress> progressMap) {
		this.progressMap = progressMap;
	}
}
