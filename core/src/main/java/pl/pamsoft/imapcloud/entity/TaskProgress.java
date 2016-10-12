package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.websocket.TaskType;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgress {
	@Id
	private String id;
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private Map<String, FileProgress> fileProgressDataMap;

	public TaskProgress() {
	}

	public void addSelectedFiles(List<FileDto> selectedFiles) {
		fileProgressDataMap = selectedFiles.stream()
			.map(file -> new FileProgress(file.getAbsolutePath(), file.getSize()))
			.collect(Collectors.toMap(FileProgress::getAbsolutePath, c -> c));
	}

	public void process(long processedBytes, String currentFileAbsolutePath, long cumulativeFileProgress) {
		this.bytesProcessed += processedBytes;
		fileProgressDataMap.get(currentFileAbsolutePath).setProgress(cumulativeFileProgress);
	}

	public void markFileProcessed(String currentFileAbsolutePath, long fileSize) {
		this.bytesProcessed = fileSize;
		fileProgressDataMap.get(currentFileAbsolutePath).setProgress(fileSize);
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

	public Map<String, FileProgress> getFileProgressDataMap() {
		return this.fileProgressDataMap;
	}

	public void setFileProgressDataMap(Map<String, FileProgress> fileProgressDataMap) {
		this.fileProgressDataMap = fileProgressDataMap;
	}
}
