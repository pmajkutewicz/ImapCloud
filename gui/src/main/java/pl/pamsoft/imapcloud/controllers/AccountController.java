package pl.pamsoft.imapcloud.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;
import pl.pamsoft.imapcloud.renderers.EmailProviderInfoRenderer;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	@Inject
	private AccountRestClient accountRestClient;

	@FXML
	private ComboBox<EmailProviderInfo> emailProvidersComboBox;

	@FXML
	private TextField passwordTextField;

	@FXML
	private TextField usernameTextField;

	@FXML
	private TableView<AccountDto> accountsTable;

	public void createButtonClick(ActionEvent event) {
		try {
			EmailProviderInfo selectedItem = emailProvidersComboBox.getSelectionModel().getSelectedItem();
			String username = usernameTextField.getText();
			String password = passwordTextField.getText();
			accountRestClient.createAccount(selectedItem, username, password);
		} catch (IOException e) {
			LOG.error("Failed", e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			emailProvidersComboBox.setButtonCell(new EmailProviderInfoRenderer());
			emailProvidersComboBox.setCellFactory(p -> new EmailProviderInfoRenderer());
			emailProvidersComboBox.getItems().addAll(accountRestClient.getAvailableEmailAccounts().getEmailProviders());
			emailProvidersComboBox.getSelectionModel().selectFirst();
			initAccountTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initAccountTable() throws IOException {
		List<AccountDto> accountDtos = accountRestClient.listAccounts();
		ObservableList<AccountDto> items = accountsTable.getItems();
		items.addAll(accountDtos);
	}
}
