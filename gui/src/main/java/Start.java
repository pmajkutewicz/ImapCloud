import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Start extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("i18n");
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/login.fxml"), bundle, new JavaFXBuilderFactory(), null);
		Scene scene = new Scene(root);
		stage.setTitle("IMAP Cloud");
		stage.setScene(scene);
		stage.show();
	}

}
