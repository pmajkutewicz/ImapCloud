package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
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

	public void process(long overallBytesProcessed, String currentFileName, long fileProgress, long fileSize) {
		this.bytesProcessed += overallBytesProcessed;
		this.currentFile = currentFileName;
		this.currentFileSize = fileSize;
		this.currentFileProgress = fileProgress;
	}

	public void markFileProcessed(String currentFileName, long fileSize) {
		this.bytesProcessed = fileSize;
		this.currentFile = currentFileName;
		this.currentFileSize = fileSize;
		this.currentFileProgress = fileSize;
	}
}
