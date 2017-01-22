package pl.pamsoft.imapcloud.controllers;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import pl.pamsoft.imapcloud.rest.GitStatusRestClient;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class StatusController implements Initializable, Refreshable{

	@Inject
	private GitStatusRestClient gitStatusRestClient;

	@FXML
	private TextArea statusText;

	@FXML
	private Node root;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initRefreshable();
	}

	@Override
	public void refresh() {
		gitStatusRestClient.getGitStatus(data -> statusText.setText(data.prettyPrint()));
	}

	@Override
	public Node getRoot() {
		return root;
	}
}
