package pl.pamsoft.imapcloud.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.controls.TaskProgressControl;
import pl.pamsoft.imapcloud.dto.progress.FileProgressDto;
import pl.pamsoft.imapcloud.responses.TaskProgressResponse;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.TaskProgressRestClient;
import pl.pamsoft.imapcloud.tools.PlatformTools;
import pl.pamsoft.imapcloud.websocket.TaskType;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TasksController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(TasksController.class);

	private static final Background BACKGROUND_DOWNLOAD = new Background(new BackgroundFill(new Color(0, 1, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background BACKGROUND_UPLOAD = new Background(new BackgroundFill(new Color(0, 1, 1, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background BACKGROUND_VERIFY = new Background(new BackgroundFill(new Color(0, 0, 1, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background BACKGROUND_RECOVERY = new Background(new BackgroundFill(new Color(1, 1, 0, 0.1), CornerRadii.EMPTY, Insets.EMPTY));
	private static final int THOUSAND = 1000;

	@FXML
	private VBox tasksContainer;

	@FXML
	private Slider updateIntervalSlider;

	//setter injection
	private PlatformTools platformTools;
	private TaskProgressRestClient taskProgressRestClient;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> currentRunningTask;
	private Map<String, TaskProgressControl> currentTasks = new HashMap<>();
	private ResourceBundle resourceBundle;

	private RequestCallback<TaskProgressResponse> getTaskCallback = result -> {
		result.getTaskProgressList().forEach(event -> {
			TaskProgressControl current;
			String taskId = event.getTaskId();
			if (!currentTasks.containsKey(taskId)) {
				current = new TaskProgressControl(taskId, parseType(event.getType()), event.getFileProgressDataMap(),
					determineBackground(event.getType()));
				currentTasks.put(taskId, current);
				platformTools.runLater(() -> tasksContainer.getChildren().addAll(current));
			} else {
				current = currentTasks.get(taskId);
			}
			double overallProgress = event.getBytesProcessed() / (double) event.getBytesOverall();

			platformTools.runLater(() -> {
					for (FileProgressDto entry : event.getFileProgressDataMap().values()) {
						current.updateProgress(entry.getAbsolutePath(), entry.getProgress(), entry.getStatus());
					}
					current.updateProgress(overallProgress);
				}
			);
			LOG.debug("Overall progress for task {} is {}", taskId, overallProgress);
		});
	};

	private Runnable updateTask = () -> taskProgressRestClient.getTasksProgress(getTaskCallback);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resourceBundle = resources;
		updateIntervalSlider.valueChangingProperty().addListener((observable, oldValue, newValue) ->
			update(updateIntervalSlider.valueProperty().doubleValue()));
	}

	private void update(double newValue) {
		platformTools.runLater(() -> {
			cancelCurrentTask();
			int schedule = (int) (newValue * THOUSAND);
			if (schedule > 0) {
				currentRunningTask = executor.scheduleWithFixedDelay(updateTask, 0, schedule, TimeUnit.MILLISECONDS);
			}
		});
	}

	private void cancelCurrentTask() {
		if (null != currentRunningTask) {
			currentRunningTask.cancel(true);
			currentRunningTask = null;
		}
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

	@Inject
	public void setPlatformTools(PlatformTools platformTools) {
		this.platformTools = platformTools;
	}

	@Inject
	public void setTaskProgressRestClient(TaskProgressRestClient taskProgressRestClient) {
		this.taskProgressRestClient = taskProgressRestClient;
	}
}
