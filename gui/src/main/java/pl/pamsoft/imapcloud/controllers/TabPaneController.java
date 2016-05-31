package pl.pamsoft.imapcloud.controllers;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TabPaneController implements Initializable {

	@FXML
	private TabPane tabPane;

	private ChangeListener<? super Tab> tabChangeListener = (ChangeListener<Tab>) (observable, closedTab, openedTab) -> {
		Parent root = (Parent) openedTab.getContent();

		Object userData = root.getUserData();
		if (userData instanceof Refreshable) {
			((Refreshable) userData).refresh();
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tabPane.getSelectionModel().clearSelection();
		tabPane.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);
	}
}
