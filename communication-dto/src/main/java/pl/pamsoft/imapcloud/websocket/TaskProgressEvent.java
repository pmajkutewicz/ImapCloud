package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgressEvent {
	private String id;
	private TaskType type;
	private String taskId;
	private long bytesOverall;
	private long bytesProcessed;
	private Map<String, FileProgressData> fileProgressDataMap;
}
