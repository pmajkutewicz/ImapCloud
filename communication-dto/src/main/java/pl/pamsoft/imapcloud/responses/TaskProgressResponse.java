package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.progress.TaskProgressDto;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class TaskProgressResponse extends AbstractResponse {

	private List<TaskProgressDto> taskProgressList;

	public TaskProgressResponse(List<TaskProgressDto> taskProgressList) {
		this.taskProgressList = taskProgressList;
	}

	public TaskProgressResponse() {
	}

	public List<TaskProgressDto> getTaskProgressList() {
		return taskProgressList;
	}

	public void setTaskProgressList(List<TaskProgressDto> taskProgressList) {
		this.taskProgressList = taskProgressList;
	}
}
