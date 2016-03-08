package pl.pamsoft.imapcloud.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pl.pamsoft.imapcloud.guice.DefaultModule;
import pl.pamsoft.imapcloud.guice.GuiceControllerFactoryCallback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

	@FXML
	private TextField host, username;

	@FXML
	private PasswordField password;

	@FXML
	private Text actiontarget;

	@FXML
	protected void handleSubmitButtonAction(ActionEvent event) throws IOException {
		ResourceBundle bundle = ResourceBundle.getBundle("i18n");
		actiontarget.setText(bundle.getString("login.connecting"));

		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();

		DefaultModule module = new DefaultModule(host.getText(), username.getText(), password.getText());
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/main.fxml"), bundle, new JavaFXBuilderFactory(), new GuiceControllerFactoryCallback(module));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("css/custom.css").toExternalForm());
		stage.setTitle("IMAP Cloud");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
