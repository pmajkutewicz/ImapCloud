package pl.pamsoft.imapcloud.renderers;

import javafx.scene.control.ListCell;
import pl.pamsoft.imapcloud.dto.AccountInfo;

public class EmailProviderInfoRenderer extends ListCell<AccountInfo> {
	@Override
	protected void updateItem(AccountInfo item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			setText('@' + item.getDomain());
		}
	}
}
