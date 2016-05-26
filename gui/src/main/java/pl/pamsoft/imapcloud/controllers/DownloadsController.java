package pl.pamsoft.imapcloud.controllers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TreeTableView;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.rest.DownloadsRestClient;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class DownloadsController implements Initializable {

	@Inject
	private DownloadsRestClient downloadsRestClient;

	@Inject
	private UploadedFileRestClient uploadedFileRestClient;

	@FXML
	private TreeTableView<UploadedFileDto> embeddedUploadedFilesTable;

	@FXML
	private FragUploadedFilesController embeddedUploadedFilesTableController;

	@FXML
	private Parent embeddedFileListTable;

	@FXML
	private FragFileListController embeddedFileListTableController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void onDownloadClick() {
		try {
			FileDto destDir = embeddedFileListTableController.getFileList().getSelectionModel().getSelectedItem();
			UploadedFileDto selectedAccountDto = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem().getValue();
			if (null != destDir && null != selectedAccountDto) {
				downloadsRestClient.startDownload(selectedAccountDto, destDir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
