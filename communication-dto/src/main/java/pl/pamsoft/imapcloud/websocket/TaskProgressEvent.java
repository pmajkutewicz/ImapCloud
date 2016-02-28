package pl.pamsoft.imapcloud.websocket;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TaskProgressEvent {
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;

	public TaskProgressEvent(String taskId, long bytesOverall) {
		this.taskId = taskId;
		this.bytesOverall = bytesOverall;
	}

	public void process(long bytesProcessed) {
		this.bytesProcessed += bytesProcessed;
	}
}
