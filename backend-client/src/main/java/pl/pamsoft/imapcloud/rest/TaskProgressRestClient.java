package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.responses.TaskProgressResponse;

public class TaskProgressRestClient extends AbstractRestClient {

	private static final String GET_TASKS = "task";

	public TaskProgressRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getTasksProgress(RequestCallback<TaskProgressResponse> callback) {
		sendGet(GET_TASKS, TaskProgressResponse.class, callback);
	}

}
