package pl.pamsoft.imapcloud.controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;
import pl.pamsoft.imapcloud.dto.UploadedFileChunkDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.responses.UploadedFileChunksResponse;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public class UploadedController implements Initializable {

	@Inject
	private UploadedFileRestClient uploadedFileRestClient;

	@FXML
	private TableView<UploadedFileDto> uploadedTable;

	@FXML
	private TableView<UploadedFileChunkDto> uploadedChunksTable;

	private ChangeListener<UploadedFileDto> uploadedFileDtoChangeListener = (observable, oldValue, newValue) -> {
		try {
			UploadedFileChunksResponse uploadedFileChunks = uploadedFileRestClient.getUploadedFileChunks(newValue.getFileUniqueId());
			uploadedChunksTable.getItems().clear();
			uploadedChunksTable.getItems().addAll(uploadedFileChunks.getFileChunks());
		} catch (IOException e) {
			e.printStackTrace();
		}
    };

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		uploadedTable.getSelectionModel().selectedItemProperty().addListener(uploadedFileDtoChangeListener);
		try {
			List<UploadedFileDto> files = uploadedFileRestClient.getUploadedFiles().getFiles();
			ObservableList<UploadedFileDto> items = uploadedTable.getItems();
			items.addAll(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteButtonClick(ActionEvent event) {
		UploadedFileDto selectedItem = uploadedTable.getSelectionModel().getSelectedItem();
		EventType<? extends ActionEvent> eventType = event.getEventType();
		System.out.println(selectedItem);
		System.out.println(eventType);
		//TODO: Delete file
	}

	public void verifyButtonClick(ActionEvent event) {
		UploadedFileDto selectedItem = uploadedTable.getSelectionModel().getSelectedItem();
		EventType<? extends ActionEvent> eventType = event.getEventType();
		System.out.println(selectedItem);
		System.out.println(eventType);
		//TODO: Verify file and chunks
	}
}
