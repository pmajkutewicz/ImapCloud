package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.progress.ProgressStatus;
import pl.pamsoft.imapcloud.websocket.TaskType;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
@Entity
public class TaskProgress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Version
	private Long version;

	private TaskType type;
	private String taskId;
	private long bytesOverall;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "task_progress_id")
	@MapKey(name = "absolutePath")
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

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
