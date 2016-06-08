package pl.pamsoft.imapcloud.controllers;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.websocket.PerformanceDataClient;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import javax.inject.Inject;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(PerformanceController.class);
	private static final int MAX_DATA_POINTS = 100;
	private static final int TEN = 10;
	private static final int TWENTY = 20;
	private static final int NB_OF_TICKS = 3;

	@Inject
	PerformanceDataClient performanceDataClient;

	@FXML
	private ToggleButton toggleButton;

	@FXML
	private VBox graphsContainer;

	private ResourceBundle bundle;

	private Map<StatisticType, ConcurrentLinkedQueue<Number>> dataQueueMap = new EnumMap<>(StatisticType.class);
	private Map<StatisticType, NumberAxis> xAxises = new EnumMap<>(StatisticType.class);
	private Map<StatisticType, NumberAxis> yAxises = new EnumMap<>(StatisticType.class);
	private Map<StatisticType, XYChart.Series<Number, Number>> series = new EnumMap<>(StatisticType.class);
	private Map<StatisticType, AtomicInteger> seriesCounter = new EnumMap<>(StatisticType.class);


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		initCharts();
		prepareTimeline();
	}

	private void initCharts() {
		for (StatisticType statisticType : StatisticType.values()) {
			dataQueueMap.put(statisticType, new ConcurrentLinkedQueue<>());
			seriesCounter.put(statisticType, new AtomicInteger(0));

			NumberAxis xAxis = createXAxis();
			NumberAxis yAxis = createYAxis();
			xAxises.put(statisticType, xAxis);
			yAxises.put(statisticType, yAxis);
			AreaChart<Number, Number> sc = createChart(statisticType, xAxis, yAxis);
			XYChart.Series<Number, Number> serie = createSeries();
			series.put(statisticType, serie);
			sc.getData().add(serie);
			graphsContainer.getChildren().addAll(sc);
		}
	}

	private AreaChart.Series<Number, Number> createSeries() {
		AreaChart.Series<Number, Number> areaSerie = new AreaChart.Series<>();
		areaSerie.setName("Area Chart Series");
		return areaSerie;
	}

	private AreaChart<Number, Number> createChart(StatisticType type, NumberAxis xAxis, NumberAxis yAxis) {
		//-- Chart
		final AreaChart<Number, Number> sc = new AreaChart<Number, Number>(xAxis, yAxis) {
			// Override to remove symbols on each data point
			@Override
			protected void dataItemAdded(Series<Number, Number> chartSerie, int itemIndex, Data<Number, Number> item) {
			}
		};
		sc.setAnimated(false);
		sc.setId(type.name());
		sc.setTitle(type.toString());
		return sc;
	}

	private NumberAxis createYAxis() {
		NumberAxis yAxis = new NumberAxis();
		yAxis.setAutoRanging(false);
		return yAxis;
	}

	private NumberAxis createXAxis() {
		NumberAxis xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / TEN);
		xAxis.setForceZeroInRange(false);
		xAxis.setAutoRanging(false);
		xAxis.setTickLabelsVisible(false);
		xAxis.setTickMarkVisible(false);
		xAxis.setMinorTickVisible(false);
		return xAxis;
	}

	//-- Timeline gets called in the JavaFX Main thread
	private void prepareTimeline() {
		// Every frame to take any data from queue and add to chart
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				addDataToSeries();
			}
		}.start();
	}

	private void addDataToSeries() {
		for (StatisticType statisticType : StatisticType.values()) {
			ConcurrentLinkedQueue<Number> queue = dataQueueMap.get(statisticType);
			XYChart.Series<Number, Number> serie = series.get(statisticType);
			AtomicInteger serieCounter = seriesCounter.get(statisticType);
			NumberAxis numberAxis = xAxises.get(statisticType);
			ObservableList<XYChart.Data<Number, Number>> data = serie.getData();
			for (int i = 0; i < TWENTY; i++) { //-- add 20 numbers to the plot+
				if (queue.isEmpty()) {
					break;
				}
				data.add(new AreaChart.Data<Number, Number>(serieCounter.getAndIncrement(), queue.remove()));
			}
			// remove points to keep us at no more than MAX_DATA_POINTS
			if (data.size() > MAX_DATA_POINTS) {
				data.remove(0, serie.getData().size() - MAX_DATA_POINTS);
			}

			updateYAxis(statisticType, data);
			updateXAxis(serieCounter, numberAxis);
		}
	}

	private void updateXAxis(AtomicInteger serieCounter, NumberAxis numberAxis) {
		numberAxis.setLowerBound(serieCounter.get() - MAX_DATA_POINTS);
		numberAxis.setUpperBound(serieCounter.get() - 1);
	}

	private void updateYAxis(StatisticType statisticType, ObservableList<XYChart.Data<Number, Number>> data) {
		NumberAxis yAxis = yAxises.get(statisticType);
		Double max = data.stream().map(XYChart.Data::getYValue).map(Number::doubleValue).max(Double::compare).orElse(0D);
		Double min = data.stream().map(XYChart.Data::getYValue).map(Number::doubleValue).min(Double::compare).orElse(0D);
		double tickUnit = (max - min) / NB_OF_TICKS;
		yAxis.setUpperBound(max);
		yAxis.setLowerBound(min);
		yAxis.setTickUnit((int)tickUnit);

	}

	public void createToggleButton(ActionEvent event) {
		// state of button after clicking, so:
		// isSelected when button become 'enabled'
		// !isSelected when button become 'disabled'
		try {
			if (toggleButton.isSelected()) {
				performanceDataClient.connect();
				performanceDataClient.addListener(this::processEvent);
				toggleButton.setText(bundle.getString("performance.button.disconnect"));
			} else {
				performanceDataClient.disconnect();
				toggleButton.setText(bundle.getString("performance.button.connect"));
			}
		} catch (IOException | URISyntaxException | DeploymentException | InterruptedException e) {
			LOG.warn("Can't connect to performance endpoint.", e);
		}
		event.consume();
	}

	private void processEvent(PerformanceDataEvent performanceEvent) {
		ConcurrentLinkedQueue<Number> numbers = dataQueueMap.get(performanceEvent.getType());
		numbers.add(performanceEvent.getCurrentValue());
	}
}
