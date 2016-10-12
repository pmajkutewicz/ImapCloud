package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgressEvent {
	private String id;
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private Map<String, FileProgressData> fileProgressDataMap;

	public TaskProgressEvent(String id, TaskType type, String taskId, long bytesOverall, long bytesProcessed, Map<String, FileProgressData> fileProgressDataMap) {
		this.id = id;
		this.type = type;
		this.taskId = taskId;
		this.bytesOverall = bytesOverall;
		this.bytesProcessed = bytesProcessed;
		this.fileProgressDataMap = fileProgressDataMap;
	}

	public TaskProgressEvent() {
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
		return this.bytesProcessed;
	}

	public void setBytesProcessed(long bytesProcessed) {
		this.bytesProcessed = bytesProcessed;
	}

	public Map<String, FileProgressData> getFileProgressDataMap() {
		return this.fileProgressDataMap;
	}

	public void setFileProgressDataMap(Map<String, FileProgressData> fileProgressDataMap) {
		this.fileProgressDataMap = fileProgressDataMap;
	}
}
