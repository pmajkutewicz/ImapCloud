package pl.pamsoft.imapcloud.controls;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public abstract class AbstractControl extends Region {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractControl.class);

	public AbstractControl() {
		this.setSnapToPixel(true);
		this.getStyleClass().add("UserControl");
		this.loadView();
	}

	private void loadView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		URL viewURL = this.getViewURL();
		loader.setLocation(viewURL);

		try {
			Node root = loader.load();
			setMaxSize(root);
			this.getChildren().add(root);
		} catch (IOException ex) {
			LOG.error("Can't load fxml", ex);
		}
	}

	private String getViewPath() {
		return String.format("/ui/controls/%s.fxml", this.getClass().getSimpleName());
	}

	@SuppressFBWarnings("UI_INHERITANCE_UNSAFE_GETRESOURCE")
	private URL getViewURL() {
		return this.getClass().getResource(this.getViewPath());
	}

	@Override
	protected void layoutChildren() {
		for (Node node : getChildren()) {
			layoutInArea(node, 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.TOP);
		}
	}

	private void setMaxSize(Node node) {
		if (node instanceof Region) {
			Region region = (Region) node;
			region.setMaxWidth(Double.MAX_VALUE);
			region.setMaxHeight(Double.MAX_VALUE);
		}
	}
}

