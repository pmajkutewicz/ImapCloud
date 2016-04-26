package pl.pamsoft.imapcloud.formatters;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class CellTrueFalseImageFormatter<S> implements Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> {

	private static final int IMG_SIZE = 16;

	@Override
	public TableCell<S, Boolean> call(TableColumn<S, Boolean> param) {
		return new TableCell<S, Boolean>() {
			@Override
			protected void updateItem(Boolean item, boolean empty) {
				if (empty && null == item) {
					setText("");
					setGraphic(null);
				} else if (!empty && null == item) {
					setText("");
					setGraphic(createIcon("unknown"));
				} else {
					setGraphic(item ? createIcon("true") : createIcon("false"));
				}
			}
		};
	}

	private ImageView createIcon(String iconFileName) {
		ImageView imageview = new ImageView();
		imageview.setFitHeight(IMG_SIZE);
		imageview.setFitWidth(IMG_SIZE);
		String fileName = String.format("img/%s.png", iconFileName);
		imageview.setImage(new Image(getClass().getClassLoader().getResource(fileName).toExternalForm()));
		return imageview;
	}
}
