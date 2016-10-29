package pl.pamsoft.imapcloud.controllers;


import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import pl.pamsoft.imapcloud.controls.MonitorChart;
import pl.pamsoft.imapcloud.responses.MonitoringResponse;
import pl.pamsoft.imapcloud.rest.MonitoringRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class MonitoringController implements Initializable, Refreshable {

	private static final int THOUSAND = 1000;

	@Inject
	private MonitoringRestClient monitoringRestClient;

	@FXML
	private Slider updateIntervalSlider;

	@FXML
	private Label sliderLabel;

	@FXML
	@SuppressWarnings("PMD.UnusedPrivateField")
	private VBox graphsContainer;

	@FXML
	private Node root;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> currentRunningTask;
	private Map<String, MonitorChart> charts = new HashMap<>();
	private long lastUpdateTimestamp = 0L;

	private RequestCallback<MonitoringResponse> getMonitorsCallback = result -> {
		lastUpdateTimestamp = result.getMonitoringTimestamp();
		result.getMonitorDatas().forEach(monitorData ->
			Platform.runLater(() -> {
				MonitorChart chart = new MonitorChart(monitorData.getMonitorKey());
				charts.put(monitorData.getMonitorKey(), chart);
				graphsContainer.getChildren().add(chart);
			})
		);
	};

	private RequestCallback<MonitoringResponse> getMonitorsAfterCallback = result -> {
		lastUpdateTimestamp = result.getMonitoringTimestamp();
		result.getMonitorDatas().forEach(monitorData -> {
			Platform.runLater(() -> {
				MonitorChart chart = charts.get(monitorData.getMonitorKey());
				chart.update(monitorData, result.getMonitoringTimestamp());
			});
		});
	};

	private Runnable updateTask = () -> {
		monitoringRestClient.getMonitorsAfter(lastUpdateTimestamp, getMonitorsAfterCallback);
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateIntervalSlider.valueChangingProperty().addListener((observable, oldValue, newValue) ->
			update(updateIntervalSlider.valueProperty().doubleValue()));
		firstTimeInit();
		initRefreshable();
	}

	private void firstTimeInit() {
		monitoringRestClient.getMonitors(getMonitorsCallback);
		sliderLabel.textProperty().bind(Bindings.format("%.2f s",updateIntervalSlider.valueProperty())
		);
	}

	private void update(double newValue) {
		Platform.runLater(() -> {
			cancelCurrentTask();
			int schedule = (int) (newValue * THOUSAND);
			if (schedule > 0) {
				currentRunningTask = executor.scheduleWithFixedDelay(updateTask, 0, schedule, TimeUnit.MILLISECONDS);
			}
		});
	}

	private void cancelCurrentTask() {
		if (null != currentRunningTask) {
			currentRunningTask.cancel(true);
			currentRunningTask = null;
		}
	}

	@Override
	public void refresh() {

	}

	@Override
	public Node getRoot() {
		return root;
	}
}
