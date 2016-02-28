package pl.pamsoft.imapcloud.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.websocket.TaskProgressClient;

import javax.inject.Inject;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class TasksController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(TasksController.class);

	@FXML
	private VBox tasksContainer;

	@Inject
	private TaskProgressClient taskProgressClient;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			taskProgressClient.connect();
		} catch (IOException | URISyntaxException | DeploymentException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
