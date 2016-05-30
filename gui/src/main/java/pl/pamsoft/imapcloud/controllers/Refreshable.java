package pl.pamsoft.imapcloud.controllers;

import javafx.scene.Node;

public interface Refreshable {
	void refresh();
	Node getRoot();
	default void initRefreshable() {
		getRoot().setUserData(this);
	}
}
