package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgressEvent {
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private Map<String, FileProgressData> fileProgressDataMap;

	public TaskProgressEvent(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles) {
		this.type = type;
		this.taskId = taskId;
		this.bytesOverall = bytesOverall;
		fileProgressDataMap = buildFileMap(selectedFiles);
	}

	public void process(long processedBytes, String currentFileAbsolutePath, long cumulativeFileProgress) {
		this.bytesProcessed += processedBytes;
		fileProgressDataMap.get(currentFileAbsolutePath).setProgress(cumulativeFileProgress);
	}

	public void markFileProcessed(String currentFileAbsolutePath, long fileSize) {
		this.bytesProcessed = fileSize;
		fileProgressDataMap.get(currentFileAbsolutePath).setProgress(fileSize);
	}

	private Map<String, FileProgressData> buildFileMap(List<FileDto> selectedFiles) {
		Map<String, FileProgressData> result = selectedFiles.stream()
			.map(file -> new FileProgressData(file.getAbsolutePath(), file.getSize()))
			.collect(Collectors.toMap(FileProgressData::getAbsolutePath, c -> c));
		return result;
	}
}
