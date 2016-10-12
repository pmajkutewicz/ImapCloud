package pl.pamsoft.imapcloud.controllers;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import pl.pamsoft.imapcloud.Utils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.rest.FilesRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.control.SelectionMode.MULTIPLE;

public class FragFileListController implements Initializable {

	@Inject
	private FilesRestClient filesRestClient;

	@Inject
	private Utils utils;

	@FXML
	private TextField currentDir;

	@FXML
	private TableView<FileDto> fileList;

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
		fileList.setOnMousePressed(doubleClickHandler);
		fileList.getSelectionModel().setSelectionMode(MULTIPLE);
		filesRestClient.getHomeDir(data -> {
			try {
				updateUI(data.getHomeDir());
			} catch (IOException e) {
				utils.showWarning(e.getMessage());
			}
		});
	}

	public void onEnterCurrentDir() {
		try {
			updateUI(currentDir.getText());
		} catch (IOException e) {
			utils.showWarning(e.getMessage());
		}
	}

	private void updateUI(String directory) throws IOException {
		currentDir.setText(directory);
		filesRestClient.listDir(directory, data -> fileList.setItems(FXCollections.observableArrayList(data.getFiles())));
	}

	public TableView<FileDto> getFileList() {
		return this.fileList;
	}
}
