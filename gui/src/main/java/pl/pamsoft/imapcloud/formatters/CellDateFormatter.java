package pl.pamsoft.imapcloud.formatters;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CellDateFormatter<S> implements Callback<TableColumn<S, Long>, TableCell<S, Long>> {
	private static final ThreadLocal<SimpleDateFormat> SDF = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	@Override
	public TableCell<S, Long> call(TableColumn<S, Long> param) {
		return new TableCell<S, Long>() {
			@Override
			protected void updateItem(Long item, boolean empty) {
				if (empty || null == item) {
					setText("");
				} else {
					setText(SDF.get().format(new Date(item)));
				}
			}
		};
	}
}
