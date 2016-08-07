package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.websocket.TaskType;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgress {
	@Id
	private String id;
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private Map<String, FileProgress> fileProgressDataMap;

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
}
