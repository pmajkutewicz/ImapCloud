package pl.pamsoft.imapcloud.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import pl.pamsoft.imapcloud.converters.AccountDtoConverter;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.RecoveryRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class RecoveryController implements Initializable, Refreshable {

	@Inject
	private AccountRestClient accountRestClient;

	@Inject
	private RecoveryRestClient recoveryRestClient;

	@FXML
	private ComboBox<AccountDto> accountsCombo;

	@FXML
	private Node root;

	private RequestCallback<ListAccountResponse> clientCallback = accounts -> {
		accountsCombo.getItems().clear();
		accountsCombo.getItems().addAll(accounts.getAccount());
		Platform.runLater(() -> accountsCombo.getSelectionModel().selectFirst());
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accountsCombo.setConverter(new AccountDtoConverter());
		initRefreshable();
	}

	@Override
	public void refresh() {
		accountRestClient.listAccounts(clientCallback);
	}

	@Override
	public Node getRoot() {
		return root;
	}

	public void startButtonClick(ActionEvent actionEvent) {
		AccountDto selectedItem = accountsCombo.getSelectionModel().getSelectedItem();
		recoveryRestClient.startUpload(selectedItem, data -> { });
	}
}
