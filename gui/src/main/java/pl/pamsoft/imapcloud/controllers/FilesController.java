package pl.pamsoft.imapcloud.controllers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class FilesController implements Initializable {

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
