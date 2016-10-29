package pl.pamsoft.imapcloud.controls;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TitledPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.monitoring.DataType;
import pl.pamsoft.imapcloud.dto.monitoring.EventData;
import pl.pamsoft.imapcloud.dto.monitoring.MonitorData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class MonitorChart extends AbstractControl {

	private static final Logger LOG = LoggerFactory.getLogger(MonitorChart.class);
	//private static final int MAX_DATA_POINTS = 10;

	@FXML
	private TitledPane titledPane;

	@FXML
	private LineChart<Long, Number> chart;

	private Map<DataType, XYChart.Series> seriesMap = new HashMap<>();

	private AtomicInteger counter = new AtomicInteger(0);

	private Function<DataType, XYChart.Series<Long, Number>> seriesGenerator = seriesType -> {
		XYChart.Series<Long, Number> series = new XYChart.Series<>();
		series.setName(seriesType.toString());
		chart.getData().add(series);
		return series;
	};

	@SuppressFBWarnings("UR_UNINIT_READ")
	public MonitorChart(String id) {
		chart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);
		titledPane.setText(id);
	}

	public void update(MonitorData monitorData, long monitoringTimestamp) {
		String value = monitorData.getMonitorKey() + " " + counter.getAndIncrement();
		titledPane.setText(value);

		for (DataType type : DataType.AGG_TYPES) {
			XYChart.Series<Long, Number> series = seriesMap.computeIfAbsent(type, seriesGenerator);

			XYChart.Data<Long, Number> entry = new XYChart.Data<>(monitoringTimestamp, monitorData.get(type));
			LOG.debug("Added {} with X: {}, Y:{}", type, monitoringTimestamp, monitorData.get(type));
			ObservableList<XYChart.Data<Long, Number>> data = series.getData();
			data.add(entry);
			//cleanUpDataSet(data);
		}
		addEvents(DataType.EVENT, monitorData.getEvents());
	}

	private void addEvents(DataType type, List<EventData> eventDatas) {
		XYChart.Series<Long, Number> series = seriesMap.computeIfAbsent(type, seriesGenerator);
		for (EventData eventData : eventDatas) {
			XYChart.Data<Long, Number> entry = new XYChart.Data<>(eventData.getTimestamp(), eventData.getValue());
			ObservableList<XYChart.Data<Long, Number>> data = series.getData();
			data.add(entry);
			LOG.debug("Added {} with X: {}, Y:{}", DataType.EVENT, eventData.getTimestamp(), eventData.getValue());
		}
		//cleanUpDataSet(series.getData());
	}

//	private void cleanUpDataSet(ObservableList<XYChart.Data<Long, Number>> data){
//		if (data.size() > MAX_DATA_POINTS) {
//			data.remove(0, data.size() - MAX_DATA_POINTS);
//		}
//	}

}
