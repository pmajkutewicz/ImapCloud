package pl.pamsoft.imapcloud.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class FragAccountsTableController implements Initializable, Refreshable {

	@Inject
	private AccountRestClient accountRestClient;

	@FXML
	private TableView<AccountDto> accountsTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refresh();
		initRefreshable();
	}

	@Override
	public void refresh() {
		accountRestClient.listAccounts(data -> {
			ObservableList<AccountDto> items = accountsTable.getItems();
			items.forEach(System.out::println);
			items.clear();
			items.addAll(data.getAccount());
		});
	}

	@Override
	public Node getRoot() {
		return accountsTable;
	}

	public AccountRestClient getAccountRestClient() {
		return this.accountRestClient;
	}

	public void setAccountRestClient(AccountRestClient accountRestClient) {
		this.accountRestClient = accountRestClient;
	}

	public TableView<AccountDto> getAccountsTable() {
		return this.accountsTable;
	}

	public void setAccountsTable(TableView<AccountDto> accountsTable) {
		this.accountsTable = accountsTable;
	}
}
