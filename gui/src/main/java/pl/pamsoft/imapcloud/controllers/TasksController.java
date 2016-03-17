package pl.pamsoft.imapcloud.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.controls.TaskProgressControl;
import pl.pamsoft.imapcloud.websocket.FileProgressData;
import pl.pamsoft.imapcloud.websocket.TaskProgressClient;
import pl.pamsoft.imapcloud.websocket.TaskProgressEvent;

import javax.inject.Inject;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TasksController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(TasksController.class);

	@FXML
	private VBox tasksContainer;

	@Inject
	private TaskProgressClient taskProgressClient;

	private Map<String, TaskProgressControl> currentTasks = new HashMap<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			taskProgressClient.addListener(this::onTaskProgressEventReceived);
			taskProgressClient.connect();
		} catch (IOException | URISyntaxException | DeploymentException | InterruptedException e) {
			LOG.error("Can't connect to backend", e);
		}
	}

	private void onTaskProgressEventReceived(TaskProgressEvent event) {
		TaskProgressControl current;
		String taskId = event.getTaskId();
		if (!currentTasks.containsKey(taskId)) {
			current = new TaskProgressControl(taskId, event.getFileProgressDataMap());
			currentTasks.put(taskId, current);
			Platform.runLater(() -> tasksContainer.getChildren().addAll(current));
		} else {
			current = currentTasks.get(taskId);
		}
		double overallProgress = event.getBytesProcessed() / (double) event.getBytesOverall();

		Platform.runLater(() -> {
				for (FileProgressData entry : event.getFileProgressDataMap().values()) {
					double fileProgress = entry.getProgress() / (double) entry.getSize();
					current.updateProgress(entry.getAbsolutePath(), entry.getProgress());
				}
				current.updateProgress(overallProgress);
			}
		);
		System.out.println(overallProgress);
	}

}
