package pl.pamsoft.imapcloud.controllers;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import pl.pamsoft.imapcloud.dto.UploadedFileChunkDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.responses.UploadedFileChunksResponse;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UploadedController implements Initializable, Refreshable {

	@Inject
	private UploadedFileRestClient uploadedFileRestClient;

	@FXML
	private TreeTableView<UploadedFileDto> embeddedUploadedFilesTable;

	@FXML
	private FragUploadedFilesController embeddedUploadedFilesTableController;

	@FXML
	private TableView<UploadedFileChunkDto> uploadedChunksTable;

	@FXML
	private Node root;

	private ChangeListener<TreeItem<UploadedFileDto>> uploadedFileDtoChangeListener = (observable, oldValue, newValue) -> {
		uploadedFileRestClient.getUploadedFileChunks(newValue.getValue().getFileUniqueId(), new RequestCallback<UploadedFileChunksResponse>() {
			@Override
			public void onFailure(IOException e) {
				markAsInvalid(uploadedChunksTable);
				uploadedChunksTable.getItems().clear();
			}

			@Override
			public void onSuccess(UploadedFileChunksResponse data) {
				markAsValid(uploadedChunksTable);
				uploadedChunksTable.getItems().clear();
				uploadedChunksTable.getItems().addAll(data.getFileChunks());
			}
		});
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		embeddedUploadedFilesTable.getSelectionModel().selectedItemProperty().addListener(uploadedFileDtoChangeListener);
		initRefreshable();
	}

	public void deleteButtonClick(ActionEvent event) {
		UploadedFileDto selectedItem = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem().getValue();
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

		//TODO: externalize strings
		alert.setTitle("Warning");
		alert.setHeaderText("Are You sure You want to delete:\n" + selectedItem.getName());

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && ButtonType.OK == result.get()) {
			uploadedFileRestClient.deleteFile(selectedItem.getFileUniqueId(), data -> {});
		}
	}

	public void verifyButtonClick(ActionEvent event) {
		UploadedFileDto selectedItem = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem().getValue();
		uploadedFileRestClient.verifyFile(selectedItem.getFileUniqueId(), data -> {});
	}

	public void resumeButtonClick(ActionEvent event) {
		UploadedFileDto selectedItem = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem().getValue();
		uploadedFileRestClient.resumeFile(selectedItem.getFileUniqueId(), data -> {});
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
