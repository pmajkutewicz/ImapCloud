import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.pamsoft.imapcloud.guice.DefaultModule;
import pl.pamsoft.imapcloud.guice.GuiceControllerFactoryCallback;

import java.util.ResourceBundle;

public class Start extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		DefaultModule module = new DefaultModule();

		ResourceBundle bundle = ResourceBundle.getBundle("i18n");
		Parent root = FXMLLoader.load(getClass().getResource("ui/main.fxml"), bundle, new JavaFXBuilderFactory(), new GuiceControllerFactoryCallback(module));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("css/custom.css").toExternalForm());
		stage.setTitle("IMAP Cloud");
		stage.setScene(scene);
		stage.show();
	}

}
