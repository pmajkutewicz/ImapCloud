package pl.pamsoft.imapcloud.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
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
	private FragFileListController embeddedFileListTableController;

	@FXML
	private Parent embeddedFileListTable;

	@FXML
	private TableView<AccountDto> embeddedAccountTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void onUploadClick() {
		try {
			ObservableList<FileDto> selectedFiles = embeddedFileListTableController.getFileList().getSelectionModel().getSelectedItems();
			AccountDto selectedAccountDto = embeddedAccountTable.getSelectionModel().getSelectedItem();
			uploadsRestClient.startUpload(selectedFiles, selectedAccountDto);
		} catch (IOException e) {
			utils.showWarning(e.getMessage());
		}

	}
}
