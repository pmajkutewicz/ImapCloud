package pl.pamsoft.imapcloud.renderers;

import javafx.scene.control.ListCell;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;

public class EmailProviderInfoRenderer extends ListCell<EmailProviderInfo> {
	@Override
	protected void updateItem(EmailProviderInfo item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			setText('@' + item.getDomain());
		}
	}
}
