package pl.pamsoft.imapcloud.requests;

public class TaskDeleteRequest {
	private String taskId;
	private boolean deleteUploadedFiles;

	public TaskDeleteRequest() {
	}

	public TaskDeleteRequest(String taskId, boolean deleteUploadedFiles) {
		this.taskId = taskId;
		this.deleteUploadedFiles = deleteUploadedFiles;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isDeleteUploadedFiles() {
		return deleteUploadedFiles;
	}

	public void setDeleteUploadedFiles(boolean deleteUploadedFiles) {
		this.deleteUploadedFiles = deleteUploadedFiles;
	}
}
