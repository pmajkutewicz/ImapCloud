package pl.pamsoft.imapcloud.controls;

import com.davidhampgonsalves.identicon.HashGeneratorInterface;
import com.davidhampgonsalves.identicon.IdenticonGenerator;
import com.davidhampgonsalves.identicon.MessageDigestHashGenerator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import pl.pamsoft.imapcloud.dto.progress.FileProgressDto;

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

	@SuppressFBWarnings("UR_UNINIT_READ")
	public TaskProgressControl(String id, String type, Map<String, FileProgressDto> fileProgressDataMap,
	                           Background background) {
		this.setBackground(background);
		this.taskIdLabel.setText(type + ' ' + id);
		fileProgressMap = createFileMap(fileProgressDataMap);
		generateIdenticon(id);
	}

	public void updateProgress(String fileAbsolutePath, double fileProgress) {
		fileProgressMap.get(fileAbsolutePath).set(fileProgress);
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

	private Map<String, SimpleDoubleProperty> createFileMap(Map<String, FileProgressDto> fileProgressDataMap) {
		Map<String, SimpleDoubleProperty> result = new ConcurrentHashMap<>(fileProgressDataMap.size());
		for (FileProgressDto fileEntry : fileProgressDataMap.values()) {
			SimpleDoubleProperty simpleDoubleProperty = new SimpleDoubleProperty(0);
			filesInfo.getChildren().add(new ProgressIndicatorBar(simpleDoubleProperty, fileEntry.getSize(), fileEntry.getAbsolutePath()));
			result.put(fileEntry.getAbsolutePath(), simpleDoubleProperty);
		}
		return result;
	}
}
