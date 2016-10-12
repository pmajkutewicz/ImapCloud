package pl.pamsoft.imapcloud.controllers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class FragUploadedFilesController implements Initializable, Refreshable {

	@Inject
	private UploadedFileRestClient uploadedFileRestClient;

	@FXML
	private TreeTableView<UploadedFileDto> embeddedUploadedFilesTable;

	private Table<Integer, String, TreeItem<UploadedFileDto>> cacheTable = HashBasedTable.create();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refresh();
		initRefreshable();
	}

	private TreeItem<UploadedFileDto> populate() {
		Consumer<UploadedFileDto> builder = dto -> {
			String[] pathSplitted = dto.getAbsolutePath().split(File.separator);
			// +1 because returned value is already created... we need to create rest of them starting from next one
			int deepestCreatedNode = findDeepestCreatedNode(pathSplitted)+1;
			int pathSize = pathSplitted.length-1;
			//last one is filename
			for (int i = deepestCreatedNode; i < pathSize; i++) {
				TreeItem<UploadedFileDto> item = new TreeItem<>(UploadedFileDto.folder(pathSplitted[i]));
				cacheTable.put(i, pathSplitted[i], item);
				cacheTable.get(i - 1, pathSplitted[i - 1]).getChildren().add(item);
			}

			cacheTable.get(pathSize-1, pathSplitted[pathSize-1]).getChildren().add(new TreeItem<>(dto));
		};

		uploadedFileRestClient.getUploadedFiles(files -> {
			cacheTable.clear();
			files.getFiles().stream().forEach(builder);
		});
		return cacheTable.get(0, "");
	}

	/**
	 * Looks for deepest created folder eg. for path /one/two/three/four/file.ext
	 * will first check if "four" node is already created, then "three" and so on.
	 *
	 * @return id of already created node. For given example 0 is "", 1 is "one" and so on.
	 */
	private int findDeepestCreatedNode(String[] pathSplitted) {
		// -2 because last part is filename and we skip it.
		for (int i = pathSplitted.length - 2; i >= 0; i--) {
			if (cacheTable.contains(i, pathSplitted[i])) {
				return i;
			}
		}
		//empty cache, lets create root node
		cacheTable.put(0, "", new TreeItem<>(UploadedFileDto.folder("")));
		return 0;
	}

	@Override
	public void refresh() {
		embeddedUploadedFilesTable.setRoot(populate());
		embeddedUploadedFilesTable.setShowRoot(false);
	}

	@Override
	public Node getRoot() {
		return embeddedUploadedFilesTable;
	}
}
