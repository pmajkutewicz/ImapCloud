package pl.pamsoft.imapcloud.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;
import pl.pamsoft.imapcloud.renderers.EmailProviderInfoRenderer;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable, Refreshable {

	@Inject
	private AccountRestClient accountRestClient;

	@FXML
	private ComboBox<EmailProviderInfo> emailProvidersComboBox;

	@FXML
	private TextField passwordTextField;

	@FXML
	private TextField usernameTextField;

	@FXML
	private TextField secretKey;

	@FXML
	private FragAccountsTableController embeddedAccountTableController;

	@FXML
	private Node root;

	public void createButtonClick(ActionEvent event) {
		EmailProviderInfo selectedItem = emailProvidersComboBox.getSelectionModel().getSelectedItem();
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();
		String cryptoKey = secretKey.getText();
		accountRestClient.createAccount(selectedItem, username, password, cryptoKey, data -> refresh());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		emailProvidersComboBox.setButtonCell(new EmailProviderInfoRenderer());
		emailProvidersComboBox.setCellFactory(p -> new EmailProviderInfoRenderer());
		accountRestClient.getAvailableEmailAccounts(data -> {
            emailProvidersComboBox.getItems().addAll(data.getEmailProviders());
            emailProvidersComboBox.getSelectionModel().selectFirst();
        });
		initRefreshable();
	}

	@Override
	public void refresh() {
		embeddedAccountTableController.refresh();
	}

	@Override
	public Node getRoot() {
		return root;
	}
}
