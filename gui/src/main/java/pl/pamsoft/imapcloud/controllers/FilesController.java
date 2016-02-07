package pl.pamsoft.imapcloud.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.responses.ListFilesInDirResponse;
import pl.pamsoft.imapcloud.rest.FilesRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FilesController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(FilesController.class);

	@Inject
	private FilesRestClient filesRestClient;

	@Inject
	private UploadsRestClient uploadsRestClient;

	@Inject
	private Utils utils;

	@FXML
	private TextField currentDir;

	@FXML
	private TableView<FileDto> fileList;

//	@FXML
//	private AccountsTableController embeddedAccountTableController;

	@FXML
	private TableView<AccountDto> embeddedAccountTable;

	private EventHandler<MouseEvent> doubleClickHandler = event -> {
		if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
			Node node = ((Node) event.getTarget()).getParent();
			TableRow<FileDto> row;
			if (node instanceof TableRow) {
				row = (TableRow<FileDto>) node;
			} else {
				// clicking on text part
				row = (TableRow<FileDto>) node.getParent();
			}
			FileDto item = row.getItem();
			try {
				updateUI(item.getAbsolutePath());
			} catch (IOException e) {
				utils.showWarning(e.getMessage());
			}
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			String homeDir = filesRestClient.getHomeDir().getHomeDir();
			fileList.setOnMousePressed(doubleClickHandler);
			fileList.getSelectionModel().setSelectionMode(
				SelectionMode.MULTIPLE
			);
			updateUI(homeDir);
		} catch (IOException e) {
			utils.showWarning(e.getMessage());
		}
	}

	public void onEnterCurrentDir() {
		try {
			updateUI(currentDir.getText());
		} catch (IOException e) {
			utils.showWarning(e.getMessage());
		}
	}

	public void onUploadClick() {
		try {
			ObservableList<FileDto> selectedFiles = fileList.getSelectionModel().getSelectedItems();
			AccountDto selectedAccountDto = embeddedAccountTable.getSelectionModel().getSelectedItem();
			uploadsRestClient.startUpload(selectedFiles, selectedAccountDto);
		} catch (IOException e) {
			utils.showWarning(e.getMessage());
		}

	}

	private void updateUI(String directory) throws IOException {
		currentDir.setText(directory);

		ListFilesInDirResponse listFilesInDirResponse = filesRestClient.listDir(directory);
		fileList.setItems(FXCollections.observableArrayList(listFilesInDirResponse.getFiles()));
	}
}
