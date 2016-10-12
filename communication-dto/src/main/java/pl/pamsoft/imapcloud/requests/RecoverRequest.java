package pl.pamsoft.imapcloud.requests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Set;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class RecoverRequest {
	private String taskId;
	private Set<String> uniqueFilesIds;

	public RecoverRequest(String taskId, Set<String> uniqueFilesIds) {
		this.taskId = taskId;
		this.uniqueFilesIds = uniqueFilesIds;
	}

	public RecoverRequest() {
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Set<String> getUniqueFilesIds() {
		return this.uniqueFilesIds;
	}

	public void setUniqueFilesIds(Set<String> uniqueFilesIds) {
		this.uniqueFilesIds = uniqueFilesIds;
	}
}
