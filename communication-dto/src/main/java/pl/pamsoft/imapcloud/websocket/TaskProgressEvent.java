package pl.pamsoft.imapcloud.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
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
