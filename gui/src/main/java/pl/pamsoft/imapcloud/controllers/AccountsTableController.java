package pl.pamsoft.imapcloud.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public class AccountsTableController implements Initializable {

	@Inject
	private AccountRestClient accountRestClient;

	@FXML
	private TableView<AccountDto> accountsTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			List<AccountDto> accountDtos = accountRestClient.listAccounts();
			ObservableList<AccountDto> items = accountsTable.getItems();
			items.addAll(accountDtos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
