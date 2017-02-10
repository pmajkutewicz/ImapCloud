package pl.pamsoft.imapcloud.controls;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pl.pamsoft.imapcloud.dto.progress.FileProgressStatus;

import java.text.DecimalFormat;

public class ProgressIndicatorBar extends StackPane {
	private static final int KIB = 1024;
	private final String[] units = new String[]{"B", "kiB", "MiB", "GiB", "TiB"};

	private final ReadOnlyDoubleProperty workDone;
	private final double totalWork;
	private final SimpleObjectProperty<FileProgressStatus> status;

	private final ProgressBar bar = new ProgressBar();
	private final Text text = new Text();
	private final String label;

	private static final int DEFAULT_LABEL_PADDING = 5;

	ProgressIndicatorBar(final ReadOnlyDoubleProperty workDone, final double totalWork, final String label, final SimpleObjectProperty<FileProgressStatus> status) {
		this.workDone = workDone;
		this.totalWork = totalWork;
		this.label = label;
		this.status = status;

		syncProgress();
		workDone.addListener((observableValue, number, number2) -> syncProgress());

		bar.setMaxWidth(Double.MAX_VALUE); // allows the progress bar to expand to fill available horizontal space.

		getChildren().setAll(bar, text);
	}

	// synchronizes the progress indicated with the work done.
	private void syncProgress() {
		text.setFill(Color.BLACK);
		bar.setStyle("-fx-accent: lightblue;");
		if (workDone == null || totalWork == 0) {
			text.setText("");
			bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		} else {
			if (FileProgressStatus.ALREADY_UPLOADED == status.getValue()) {
				text.setText(String.format("%s (%s)", label, status.getValue().toString()));
			} else {
				text.setText(String.format("%s (%s of %s)", label, getReadableFileSize(workDone.get()), getReadableFileSize(totalWork)));
			}
			bar.setProgress(workDone.get() / totalWork);
		}

		bar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
		bar.setMinWidth(text.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
	}

	public String getReadableFileSize(double size) {
		if (size <= 0) {
			return "0";
		}
		int digitGroups = (int) (Math.log10(size) / Math.log10(KIB));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(KIB, digitGroups)) + " " + units[digitGroups];
	}
}
