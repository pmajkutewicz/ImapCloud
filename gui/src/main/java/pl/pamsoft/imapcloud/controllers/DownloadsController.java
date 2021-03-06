package pl.pamsoft.imapcloud.controllers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeTableView;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.rest.DownloadsRestClient;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class DownloadsController implements Initializable, Refreshable {

	@Inject
	private DownloadsRestClient downloadsRestClient;

	@FXML
	private TreeTableView<UploadedFileDto> embeddedUploadedFilesTable;

	@FXML
	private FragUploadedFilesController embeddedUploadedFilesTableController;

	@FXML
	@SuppressWarnings("PMD.UnusedPrivateField")
	private Parent embeddedFileListTable;

	@FXML
	private FragFileListController embeddedFileListTableController;

	@FXML
	private Node root;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initRefreshable();
	}

	public void onDownloadClick() {
		FileDto destDir = embeddedFileListTableController.getFileList().getSelectionModel().getSelectedItem();
		UploadedFileDto selectedAccountDto = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem().getValue();
		if (null != destDir && null != selectedAccountDto) {
			downloadsRestClient.startDownload(selectedAccountDto, destDir, data -> { });
		}
	}

	@Override
	public void refresh() {
		embeddedUploadedFilesTableController.refresh();
	}

	@Override
	public Node getRoot() {
		return root;
	}
}
