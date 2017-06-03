package pl.pamsoft.imapcloud.controls;

import com.davidhampgonsalves.identicon.HashGeneratorInterface;
import com.davidhampgonsalves.identicon.IdenticonGenerator;
import com.davidhampgonsalves.identicon.MessageDigestHashGenerator;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import pl.pamsoft.imapcloud.dto.progress.FileProgressDto;
import pl.pamsoft.imapcloud.dto.progress.FileProgressStatus;

import java.util.Map;
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

	private Map<String, SimpleDoubleProperty> fileProgressMap;
	private Map<String, SimpleObjectProperty<FileProgressStatus>> fileProgressStatusMap;

	@SuppressFBWarnings("UR_UNINIT_READ")
	public TaskProgressControl(String id, String type, Map<String, FileProgressDto> fileProgressDataMap,
	                           Background background) {
		this.setBackground(background);
		this.taskIdLabel.setText(type + ' ' + id);
		createFileMap(fileProgressDataMap);
		generateIdenticon(id);
	}

	public void updateProgress(String fileAbsolutePath, double fileProgress, FileProgressStatus status) {
		fileProgressMap.get(fileAbsolutePath).set(fileProgress);
		if (FileProgressStatus.ALREADY_UPLOADED == status) {
			fileProgressStatusMap.get(fileAbsolutePath).setValue(FileProgressStatus.ALREADY_UPLOADED);
		}
	}

	public void updateProgress(double overallProgressValue) {
		overallProgress.setProgress(overallProgressValue);
	}


	private void generateIdenticon(String hash) {
		HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("sha-512");
		WritableImage writableImage = IdenticonGenerator.generateWithoutSmoothing(hash, hashGenerator);

		identicon.setSmooth(false);
		identicon.setPreserveRatio(true);
		identicon.setImage(writableImage);
	}

	private void createFileMap(Map<String, FileProgressDto> fileProgressDataMap) {
		fileProgressMap = new ConcurrentHashMap<>(fileProgressDataMap.size());
		fileProgressStatusMap = new ConcurrentHashMap<>(fileProgressDataMap.size());

		for (FileProgressDto fileEntry : fileProgressDataMap.values()) {
			SimpleDoubleProperty simpleDoubleProperty = new SimpleDoubleProperty(0);
			SimpleObjectProperty<FileProgressStatus> statusProperty = new SimpleObjectProperty<>(fileEntry.getStatus());
			ProgressIndicatorBar indicatorBar = new ProgressIndicatorBar(simpleDoubleProperty, fileEntry.getSize(), fileEntry.getAbsolutePath(), statusProperty);
			filesInfo.getChildren().add(indicatorBar);
			fileProgressMap.put(fileEntry.getAbsolutePath(), simpleDoubleProperty);
			fileProgressStatusMap.put(fileEntry.getAbsolutePath(), statusProperty);
		}
	}

	@VisibleForTesting
	public ProgressIndicator getOverallProgress() {
		return overallProgress;
	}

	@VisibleForTesting
	public Map<String, SimpleDoubleProperty> getFileProgressMap() {
		return fileProgressMap;
	}
}
