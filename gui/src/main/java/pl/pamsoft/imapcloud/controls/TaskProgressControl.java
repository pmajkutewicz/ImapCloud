package pl.pamsoft.imapcloud.controls;

import com.davidhampgonsalves.identicon.HashGeneratorInterface;
import com.davidhampgonsalves.identicon.IdenticonGenerator;
import com.davidhampgonsalves.identicon.MessageDigestHashGenerator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class TaskProgressControl extends AbstractControl {

	@FXML
	private Label taskIdLabel;
	@FXML
	private ProgressBar currentFileProgress;
	@FXML
	private Label taskMessage;
	@FXML
	private ProgressIndicator overallProgress;
	@FXML
	private ImageView identicon;

	@SuppressFBWarnings("UR_UNINIT_READ")
	public TaskProgressControl(String id) {
		this.taskIdLabel.setText(id);
		generateIdenticon(id);
	}

	public void updateProgress(double overallProgressValue, double currentFileProgressValue, String message) {
		currentFileProgress.setProgress(currentFileProgressValue);
		overallProgress.setProgress(overallProgressValue);
		taskMessage.setText(message);
	}


	private void generateIdenticon(String hash) {
		HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("sha-512");
		WritableImage writableImage = IdenticonGenerator.generateWithoutSmoothing(hash, hashGenerator);

		identicon.setSmooth(false);
		identicon.setPreserveRatio(true);
		identicon.setImage(writableImage);
	}

}
