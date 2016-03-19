package pl.pamsoft.imapcloud.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
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
	private TableView<FileDto> uploadedTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			List<UploadedFileDto> files = uploadedFileRestClient.getUploadedFiles().getFiles();
			ObservableList<FileDto> items = uploadedTable.getItems();
			items.addAll(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
