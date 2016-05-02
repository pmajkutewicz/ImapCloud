package pl.pamsoft.imapcloud.controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import pl.pamsoft.imapcloud.dto.UploadedFileChunkDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.responses.UploadedFileChunksResponse;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UploadedController implements Initializable {

	@Inject
	private UploadedFileRestClient uploadedFileRestClient;

	@FXML
	private TableView<UploadedFileDto> embeddedUploadedFilesTable;

	@FXML
	private TableView<UploadedFileChunkDto> uploadedChunksTable;

	private ChangeListener<UploadedFileDto> uploadedFileDtoChangeListener = (observable, oldValue, newValue) -> {
		try {
			UploadedFileChunksResponse uploadedFileChunks = uploadedFileRestClient.getUploadedFileChunks(newValue.getFileUniqueId());
			uploadedChunksTable.getItems().clear();
			uploadedChunksTable.getItems().addAll(uploadedFileChunks.getFileChunks());
		} catch (IOException e) {
			uploadedChunksTable.getItems().clear();
			e.printStackTrace();
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		embeddedUploadedFilesTable.getSelectionModel().selectedItemProperty().addListener(uploadedFileDtoChangeListener);
		try {
			List<UploadedFileDto> files = uploadedFileRestClient.getUploadedFiles().getFiles();
			ObservableList<UploadedFileDto> items = embeddedUploadedFilesTable.getItems();
			items.addAll(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteButtonClick(ActionEvent event) {
		UploadedFileDto selectedItem = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem();
		try {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

			//TODO: externalize strings
			alert.setTitle("Warning");
			alert.setHeaderText("Are You sure You want to delete:\n" + selectedItem.getName());

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && ButtonType.OK == result.get()) {
				uploadedFileRestClient.deleteFile(selectedItem.getFileUniqueId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void verifyButtonClick(ActionEvent event) {
		try {
			UploadedFileDto selectedItem = embeddedUploadedFilesTable.getSelectionModel().getSelectedItem();
			uploadedFileRestClient.verifyFile(selectedItem.getFileUniqueId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
