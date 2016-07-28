package pl.pamsoft.imapcloud.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import pl.pamsoft.imapcloud.converters.AccountDtoConverter;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.RecoveredFileDto;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;
import pl.pamsoft.imapcloud.responses.RecoveryResultsResponse;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.RecoveryRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;

import javax.inject.Inject;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static javafx.scene.control.SelectionMode.MULTIPLE;

public class RecoveryController implements Initializable, Refreshable {

	@Inject
	private AccountRestClient accountRestClient;

	@Inject
	private RecoveryRestClient recoveryRestClient;

	@FXML
	private ComboBox<AccountDto> accountsCombo;

	@FXML
	private ComboBox<String> availableRecoveriesCombo;

	@FXML
	private TableView<FileDto> fileList;

	@FXML
	private Node root;

	private Map<String, List<RecoveredFileDto>> recoveriesData = Collections.emptyMap();

	private RequestCallback<ListAccountResponse> clientCallback = accounts -> {
		Platform.runLater(() -> {
			accountsCombo.getItems().clear();
			accountsCombo.getItems().addAll(accounts.getAccount());
			accountsCombo.getSelectionModel().selectFirst();
		});
	};

	private RequestCallback<RecoveryResultsResponse> resultsCallback = results -> {
		recoveriesData = results.getRecoveredFiles();
		Platform.runLater(() -> {
			availableRecoveriesCombo.getItems().clear();
			availableRecoveriesCombo.getItems().addAll(recoveriesData.keySet());
			availableRecoveriesCombo.getSelectionModel().selectFirst();
		});
	};

	private ChangeListener<String> availableRecoveriesComboChangeListener = (observable, oldValue, newValue) -> {
		if (null != newValue && newValue != oldValue) {
			System.out.println("Changed to: " + newValue);
			List<RecoveredFileDto> data = recoveriesData.get(newValue);
			fileList.setItems(FXCollections.observableArrayList(data));
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accountsCombo.setConverter(new AccountDtoConverter());
		availableRecoveriesCombo.valueProperty().addListener(availableRecoveriesComboChangeListener);
		fileList.getSelectionModel().setSelectionMode(MULTIPLE);
		initRefreshable();
	}

	@Override
	public void refresh() {
		accountRestClient.listAccounts(clientCallback);
		recoveryRestClient.getResults(resultsCallback);
	}

	@Override
	public Node getRoot() {
		return root;
	}

	public void startButtonClick(ActionEvent actionEvent) {
		AccountDto selectedItem = accountsCombo.getSelectionModel().getSelectedItem();
		recoveryRestClient.startAccountRecovery(selectedItem, data -> {});
	}

	public void recoverButtonClick(ActionEvent actionEvent) {

	}
}
