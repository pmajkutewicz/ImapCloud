package pl.pamsoft.imapcloud.controllers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;
import pl.pamsoft.imapcloud.tools.PlatformTools;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static pl.pamsoft.imapcloud.requests.Encryption.OFF;
import static pl.pamsoft.imapcloud.requests.Encryption.ON;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class UploadsController implements Initializable, Refreshable {

	@Inject
	private UploadsRestClient uploadsRestClient;

	@Inject
	private AccountRestClient accountRestClient;

	@Inject
	private Utils utils;

	@Inject
	private PlatformTools platformTools;

	@FXML
	private FragFileListController embeddedFileListTableController;

	@FXML
	@SuppressWarnings("PMD.UnusedPrivateField")
	private Parent embeddedFileListTable;

	@FXML
	private ComboBox<AccountDto> destinationAccount;

	@FXML
	private CheckBox encrypt;

	@FXML
	private Node root;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initRefreshable();
	}

	public void onUploadClick() {
		ObservableList<FileDto> selectedFiles = embeddedFileListTableController.getFileList().getSelectionModel().getSelectedItems();
		AccountDto selectedAccountDto = destinationAccount.getSelectionModel().getSelectedItem();
		uploadsRestClient.startUpload(selectedFiles, selectedAccountDto, encrypt.isSelected() ? ON : OFF, new RequestCallback<Void>() {
			@Override
			public void onFailure(IOException e) {
				utils.showWarning(e.getMessage());
			}

			@Override
			public void onSuccess(Void data) { }
		});
	}

	@Override
	public void refresh() {
		accountRestClient.listAccounts(data -> platformTools.runLater(() -> {
			destinationAccount.getItems().addAll(data.getAccount());
			destinationAccount.getSelectionModel().selectFirst();
		}));
	}

	@Override
	public Node getRoot() {
		return root;
	}
}
