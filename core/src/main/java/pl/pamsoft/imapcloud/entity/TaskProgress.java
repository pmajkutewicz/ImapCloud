package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.progress.FileProgressStatus;
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
	private Map<String, FileProgress> fileProgressDataMap;

	public void process(String currentFileAbsolutePath, long cumulativeFileProgress) {
		FileProgress fileProgress = fileProgressDataMap.get(currentFileAbsolutePath);
		fileProgress.setProgress(cumulativeFileProgress);
		if (cumulativeFileProgress == fileProgress.getSize()){
			fileProgress.setStatus(FileProgressStatus.UPLOADED);
		} else {
			fileProgress.setStatus(FileProgressStatus.UPLOADING);
		}
	}

	public void markFileAlreadyUploaded(String currentFileAbsolutePath, long fileSize) {
		FileProgress fileProgress = fileProgressDataMap.get(currentFileAbsolutePath);
		fileProgress.setProgress(fileSize);
		fileProgress.setStatus(FileProgressStatus.ALREADY_UPLOADED);
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

	public long getBytesProcessed() {
		return fileProgressDataMap.values().stream().mapToLong(FileProgress::getProgress).sum();
	}

	public Map<String, FileProgress> getFileProgressDataMap() {
		return this.fileProgressDataMap;
	}

	public void setFileProgressDataMap(Map<String, FileProgress> fileProgressDataMap) {
		this.fileProgressDataMap = fileProgressDataMap;
	}
}
