package pl.pamsoft.imapcloud.controls;

import com.davidhampgonsalves.identicon.HashGeneratorInterface;
import com.davidhampgonsalves.identicon.IdenticonGenerator;
import com.davidhampgonsalves.identicon.MessageDigestHashGenerator;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

public class TaskProgressControl extends AbstractControl {

	@FXML
	private ProgressBar progressBar;
	@FXML
	private ImageView identicon;

	private String taskId;

	public TaskProgressControl(String taskId) {
		this.taskId = taskId;
		updateIdenticon(taskId);
	}

	private void updateIdenticon(String taskId) {
		HashGeneratorInterface hashGenerator = new MessageDigestHashGenerator("sha-512");
		BufferedImage awtImage = IdenticonGenerator.generate(taskId, hashGenerator);

		identicon.setSmooth(false);
		identicon.setPreserveRatio(true);
		WritableImage fxImage = new WritableImage(awtImage.getWidth(), awtImage.getHeight());
		SwingFXUtils.toFXImage(awtImage, fxImage);
		identicon.setImage(fxImage);
	}

}
