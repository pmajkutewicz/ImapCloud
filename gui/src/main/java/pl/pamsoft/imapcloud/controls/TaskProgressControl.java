package pl.pamsoft.imapcloud.controls;

import com.davidhampgonsalves.identicon.HashGeneratorInterface;
import com.davidhampgonsalves.identicon.IdenticonGenerator;
import com.davidhampgonsalves.identicon.MessageDigestHashGenerator;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import pl.pamsoft.imapcloud.dto.progress.EntryProgressDto;
import pl.pamsoft.imapcloud.dto.progress.ProgressStatus;
import pl.pamsoft.imapcloud.rest.TaskProgressRestClient;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class TaskProgressControl extends AbstractControl {

	@FXML
	private Label taskIdLabel;
	@FXML
	@SuppressWarnings("PMD.UnusedPrivateField")
	private Label taskMessage;
	@FXML
	private ProgressIndicator overallProgress;
	@FXML
	private ImageView identicon;
	@FXML
	private VBox filesInfo;

	private String taskId;
	private TaskProgressRestClient taskProgressRestClient;
	private Map<String, SimpleDoubleProperty> progressMap;
	private Map<String, SimpleObjectProperty<ProgressStatus>> progressStatusMap;

	@SuppressFBWarnings("UR_UNINIT_READ")
	public TaskProgressControl(String id, String type, Map<String, EntryProgressDto> progressDtoMap,
	                           Background background, TaskProgressRestClient taskProgressRestClient) {
		this.taskProgressRestClient = taskProgressRestClient;
		this.setBackground(background);
		this.taskId = id;
		this.taskIdLabel.setText(type + ' ' + id);
		createFileMap(progressDtoMap);
		generateIdenticon(id);
	}

	public void updateProgress(String fileAbsolutePath, double progress, ProgressStatus status) {
		progressMap.get(fileAbsolutePath).set(progress);
		if (status.isTaskCompleted()) {
			progressStatusMap.get(fileAbsolutePath).setValue(status);
		}
	}

	public void updateProgress(double overallProgressValue) {
		overallProgress.setProgress(overallProgressValue);
	}

	@FXML
	public void deleteButtonClick(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Are you sure?");
		alert.setHeaderText("Do You also want to delete all uploaded data?");
		alert.initStyle(StageStyle.UTILITY);

		ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
		ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(btnYes, btnNo, btnCancel);


		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == btnYes) {
			taskProgressRestClient.deleteTask(taskId, true, data -> { });
		} else if (result.get() == btnNo) {
			taskProgressRestClient.deleteTask(taskId, false, data -> { });
		}
	}

	private void generateIdenticon(String hash) {
		HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("sha-512");
		WritableImage writableImage = IdenticonGenerator.generateWithoutSmoothing(hash, hashGenerator);

		identicon.setSmooth(false);
		identicon.setPreserveRatio(true);
		identicon.setImage(writableImage);
	}

	private void createFileMap(Map<String, EntryProgressDto> progressDtoMap) {
		progressMap = new ConcurrentHashMap<>(progressDtoMap.size());
		progressStatusMap = new ConcurrentHashMap<>(progressDtoMap.size());

		for (EntryProgressDto entry : progressDtoMap.values()) {
			SimpleDoubleProperty simpleDoubleProperty = new SimpleDoubleProperty(0);
			SimpleObjectProperty<ProgressStatus> statusProperty = new SimpleObjectProperty<>(entry.getStatus());
			ProgressIndicatorBar indicatorBar = new ProgressIndicatorBar(simpleDoubleProperty, statusProperty, entry);
			filesInfo.getChildren().add(indicatorBar);
			progressMap.put(entry.getAbsolutePath(), simpleDoubleProperty);
			progressStatusMap.put(entry.getAbsolutePath(), statusProperty);
		}
	}

	@VisibleForTesting
	public ProgressIndicator getOverallProgress() {
		return overallProgress;
	}

	@VisibleForTesting
	public Map<String, SimpleDoubleProperty> getProgressMap() {
		return progressMap;
	}
}
