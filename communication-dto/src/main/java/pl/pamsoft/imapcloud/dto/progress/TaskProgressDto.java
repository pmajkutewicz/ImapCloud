package pl.pamsoft.imapcloud.dto.progress;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgressDto {

	private Long id;
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private Map<String, EntryProgressDto> progressMap;

	public TaskProgressDto(Long id, TaskType type, String taskId, long bytesOverall, long bytesProcessed, Map<String, EntryProgressDto> progressMap) {
		this.id = id;
		this.type = type;
		this.taskId = taskId;
		this.bytesOverall = bytesOverall;
		this.bytesProcessed = bytesProcessed;
		this.progressMap = progressMap;
	}

	public TaskProgressDto() {
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

	public long getBytesProcessed() {
		return this.bytesProcessed;
	}

	public void setBytesProcessed(long bytesProcessed) {
		this.bytesProcessed = bytesProcessed;
	}

	public Map<String, EntryProgressDto> getProgressMap() {
		return this.progressMap;
	}

	public void setProgressMap(Map<String, EntryProgressDto> progressMap) {
		this.progressMap = progressMap;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TaskProgressDto{");
		sb.append("id='").append(id).append('\'');
		sb.append(", type=").append(type);
		sb.append(", taskId='").append(taskId).append('\'');
		sb.append(", bytesOverall=").append(bytesOverall);
		sb.append(", bytesProcessed=").append(bytesProcessed);
		sb.append(", progressMap=").append(progressMap);
		sb.append('}');
		return sb.toString();
	}
}
