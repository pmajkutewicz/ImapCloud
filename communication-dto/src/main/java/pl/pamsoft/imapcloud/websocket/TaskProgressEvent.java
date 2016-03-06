package pl.pamsoft.imapcloud.websocket;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TaskProgressEvent {
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private String currentFile;
	private long currentFileSize;
	private long currentFileProgress;

	public TaskProgressEvent(String taskId, long bytesOverall) {
		this.taskId = taskId;
		this.bytesOverall = bytesOverall;
	}

	public void process(long bytesProcessed, String currentFile, long currentFileProgress, long currentFileSize) {
		this.bytesProcessed += bytesProcessed;
		this.currentFile = currentFile;
		this.currentFileSize = currentFileSize;
		this.currentFileProgress = currentFileProgress;
	}
}
