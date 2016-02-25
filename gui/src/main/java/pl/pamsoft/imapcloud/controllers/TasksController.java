package pl.pamsoft.imapcloud.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class TasksController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(TasksController.class);

	@FXML
	private VBox tasksContainer;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
