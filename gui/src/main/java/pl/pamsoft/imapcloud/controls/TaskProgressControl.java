package pl.pamsoft.imapcloud.controls;

import com.davidhampgonsalves.identicon.HashGeneratorInterface;
import com.davidhampgonsalves.identicon.IdenticonGenerator;
import com.davidhampgonsalves.identicon.MessageDigestHashGenerator;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public class TaskProgressControl extends AbstractControl {

	@FXML
	private Label taskIdLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private ImageView identicon;

	private String taskId;

	public TaskProgressControl(String taskId) {
		this.taskId = taskId;
		this.taskIdLabel.setText(taskId);
		generateIdenticon(taskId);
	}

	public void updateProgress(double value) {
		progressBar.setProgress(value);
	}


	private void generateIdenticon(String taskId) {
		HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("sha-512");
		BufferedImage awtImage = IdenticonGenerator.generate(taskId, hashGenerator);

		identicon.setSmooth(false);
		identicon.setPreserveRatio(true);
		WritableImage fxImage = new WritableImage(awtImage.getWidth(), awtImage.getHeight());
		SwingFXUtils.toFXImage(awtImage, fxImage);
		identicon.setImage(fxImage);
	}

}
