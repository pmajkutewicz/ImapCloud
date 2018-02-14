package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.requests.TaskDeleteRequest;
import pl.pamsoft.imapcloud.responses.TaskProgressResponse;

public class TaskProgressRestClient extends AbstractRestClient {

	private static final String GET_TASKS = "task/progress";
	private static final String DELETE_TASKS = "task/delete";

	public TaskProgressRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getTasksProgress(RequestCallback<TaskProgressResponse> callback) {
		sendGet(GET_TASKS, TaskProgressResponse.class, callback);
	}

	public void deleteTask(String taskId, boolean deleteUploadedFiles, RequestCallback<Void> callback) {
		sendPost(DELETE_TASKS, new TaskDeleteRequest(taskId, deleteUploadedFiles), callback);
	}

}
