package pl.pamsoft.imapcloud.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class PerformanceController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(PerformanceController.class);

	@FXML
	private ToggleButton toggleButton;

	private ResourceBundle bundle;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
	}

	public void createToggleButton(ActionEvent event) {
		if (toggleButton.isSelected()) {
			toggleButton.setText(bundle.getString("performance.button.disconnect"));
		} else {
			toggleButton.setText(bundle.getString("performance.button.connect"));
		}
	}
}
