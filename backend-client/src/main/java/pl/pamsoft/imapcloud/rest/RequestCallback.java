package pl.pamsoft.imapcloud.rest;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.layout.BackgroundPosition.CENTER;
import static javafx.scene.layout.BackgroundRepeat.NO_REPEAT;

public interface RequestCallback<T> {

	Logger LOG = LoggerFactory.getLogger(RequestCallback.class);

	void onSuccess(T data) throws IOException;

	default void onFailure(IOException e) {
		LOG.error("Unhandled error", e);
		e.printStackTrace();
	}

	default void markAsInvalid(Region region) {
		ImageView imageview = new ImageView();
		String fileName = "img/disconnected.png";
		imageview.setImage(new Image(getClass().getClassLoader().getResource(fileName).toExternalForm()));
		Background background = new Background(new BackgroundImage(imageview.getImage(), NO_REPEAT, NO_REPEAT, CENTER, null));
		region.setBackground(background);
	}

	default void markAsValid(Region region) {
		region.setBackground(EMPTY);
	}
}
