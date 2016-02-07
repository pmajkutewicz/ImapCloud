package pl.pamsoft.imapcloud;

import javafx.scene.control.Alert;

public class Utils {

	public void showWarning(String message) {
		//http://code.makery.ch/blog/javafx-dialogs-official/
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
