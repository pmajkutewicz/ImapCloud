package pl.pamsoft.imapcloud.controllers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.rest.DownloadsRestClient;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class DownloadsController implements Initializable {

	@Inject
	private DownloadsRestClient downloadsRestClient;

	@Inject
	private UploadedFileRestClient uploadedFileRestClient;

	@FXML
	private TableView<UploadedFileDto> embeddedUploadedFilesTable;

	@FXML
	private FragFileListController embeddedFileListTableController;

	@FXML
	private Parent embeddedFileListTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			List<UploadedFileDto> files = uploadedFileRestClient.getUploadedFiles().getFiles();
			ObservableList<UploadedFileDto> items = embeddedUploadedFilesTable.getItems();
			items.addAll(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onDownloadClick() {
		try {
			FileDto destDir = embeddedFileListTableController.getFileList().getSelectionModel().getSelectedItem();
			UploadedFileDto selectedAccountDto = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem();
			downloadsRestClient.startDownload(selectedAccountDto, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
