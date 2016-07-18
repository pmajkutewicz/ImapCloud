package pl.pamsoft.imapcloud.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import pl.pamsoft.imapcloud.converters.AccountDtoConverter;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;
import pl.pamsoft.imapcloud.responses.RecoveryResultsResponse;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.RecoveryRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RecoveryController implements Initializable, Refreshable {

	@Inject
	private AccountRestClient accountRestClient;

	@Inject
	private RecoveryRestClient recoveryRestClient;

	@FXML
	private ComboBox<AccountDto> accountsCombo;

	@FXML
	private ComboBox<String> availableRecoveries;

	@FXML
	private TableView<?> fileList;

	@FXML
	private Node root;

	private Map<String, JsonNode> recoveriesData = new HashMap<>();

	private RequestCallback<ListAccountResponse> clientCallback = accounts -> {
		Platform.runLater(() -> {
			accountsCombo.getItems().clear();
			accountsCombo.getItems().addAll(accounts.getAccount());
			accountsCombo.getSelectionModel().selectFirst();
		});
	};

	private RequestCallback<RecoveryResultsResponse> resultsCallback = results -> {
		results.getResults().entrySet().forEach(e -> {
			byte[] value = e.getValue();
			if (null != value) {
				recoveriesData.put(e.getKey(), unpack(value));
			}
			Platform.runLater(() -> {
				availableRecoveries.getItems().clear();
				availableRecoveries.getItems().addAll(recoveriesData.keySet());
				availableRecoveries.getSelectionModel().selectFirst();
			});
		});
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accountsCombo.setConverter(new AccountDtoConverter());
		availableRecoveries.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != oldValue) {
				System.out.println("Changed to: " + newValue);
			}
		});

		//fileList.getSelectionModel().selectedItemProperty().addListener(?);
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

	private JsonNode unpack(byte[] data) {
		byte[] decoded = Base64.getDecoder().decode(data);
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(decoded));
		try {
			ZipEntry nextEntry = zipInputStream.getNextEntry();
			if (null != nextEntry) {
				return new ObjectMapper().readTree(zipInputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
