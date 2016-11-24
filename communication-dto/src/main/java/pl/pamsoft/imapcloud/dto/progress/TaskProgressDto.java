package pl.pamsoft.imapcloud.dto.progress;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgressDto {

	private String id;
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private Map<String, FileProgressDto> fileProgressDataMap;

	public TaskProgressDto(String id, TaskType type, String taskId, long bytesOverall, long bytesProcessed, Map<String, FileProgressDto> fileProgressDataMap) {
		this.id = id;
		this.type = type;
		this.taskId = taskId;
		this.bytesOverall = bytesOverall;
		this.bytesProcessed = bytesProcessed;
		this.fileProgressDataMap = fileProgressDataMap;
	}

	public TaskProgressDto() {
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

	public Map<String, FileProgressDto> getFileProgressDataMap() {
		return this.fileProgressDataMap;
	}

	public void setFileProgressDataMap(Map<String, FileProgressDto> fileProgressDataMap) {
		this.fileProgressDataMap = fileProgressDataMap;
	}
}
