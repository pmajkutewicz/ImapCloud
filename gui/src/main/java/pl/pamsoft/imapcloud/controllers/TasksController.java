package pl.pamsoft.imapcloud.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.controls.TaskProgressControl;
import pl.pamsoft.imapcloud.websocket.FileProgressData;
import pl.pamsoft.imapcloud.websocket.TaskProgressClient;
import pl.pamsoft.imapcloud.websocket.TaskProgressEvent;
import pl.pamsoft.imapcloud.websocket.TaskType;

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

	private static final Background BACKGROUND_DOWNLOAD = new Background(new BackgroundFill(new Color(0, 1, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background BACKGROUND_UPLOAD = new Background(new BackgroundFill(new Color(0, 1, 1, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background BACKGROUND_VERIFY = new Background(new BackgroundFill(new Color(0, 0, 1, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background BACKGROUND_RECOVERY = new Background(new BackgroundFill(new Color(1, 1, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY));

	@FXML
	private VBox tasksContainer;

	@Inject
	private TaskProgressClient taskProgressClient;

	private Map<String, TaskProgressControl> currentTasks = new HashMap<>();
	private ResourceBundle resourceBundle;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resourceBundle = resources;
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
			current = new TaskProgressControl(taskId, parseType(event.getType()), event.getFileProgressDataMap(),
				determineBackground(event.getType()));
			currentTasks.put(taskId, current);
			Platform.runLater(() -> tasksContainer.getChildren().addAll(current));
		} else {
			current = currentTasks.get(taskId);
		}
		double overallProgress = event.getBytesProcessed() / (double) event.getBytesOverall();

		Platform.runLater(() -> {
				for (FileProgressData entry : event.getFileProgressDataMap().values()) {
					current.updateProgress(entry.getAbsolutePath(), entry.getProgress());
				}
				current.updateProgress(overallProgress);
			}
		);
		System.out.println(overallProgress);
	}

	private Background determineBackground(TaskType taskType) {
		switch (taskType) {
			case DOWNLOAD:
				return BACKGROUND_DOWNLOAD;
			case UPLOAD:
				return BACKGROUND_UPLOAD;
			case VERIFY:
				return BACKGROUND_VERIFY;
			case RECOVERY:
				return BACKGROUND_RECOVERY;
			default:
				return Background.EMPTY;
		}
	}

	private String parseType(TaskType taskType) {
		return resourceBundle.getString("task.type." + taskType.toString().toLowerCase());
	}

}
