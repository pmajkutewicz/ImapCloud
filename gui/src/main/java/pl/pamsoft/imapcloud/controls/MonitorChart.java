package pl.pamsoft.imapcloud.controls;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TitledPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.monitoring.DataType;
import pl.pamsoft.imapcloud.dto.monitoring.EventData;
import pl.pamsoft.imapcloud.dto.monitoring.MonitorData;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class MonitorChart extends AbstractControl {

	private static final Logger LOG = LoggerFactory.getLogger(MonitorChart.class);
	private static final int NB_OF_TICKS = 10;

	@FXML
	private TitledPane titledPane;

	@FXML
	private LineChart<Long, Number> chart;

	private Map<DataType, XYChart.Series> seriesMap = new HashMap<>();

	private AtomicInteger counter = new AtomicInteger(0);
	private TemporalUnit cutOffUnitOld;
	private int cutOffValueOld;

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

	public void update(MonitorData monitorData, long monitoringTimestamp, TemporalUnit cutOffUnit, int cutOffValue) {
		String value = monitorData.getMonitorKey() + " " + counter.getAndIncrement();
		titledPane.setText(value);

		long limit = Instant.now().minus(cutOffValue, cutOffUnit).toEpochMilli();
		for (DataType type : DataType.AGG_TYPES) {
			XYChart.Series<Long, Number> series = seriesMap.computeIfAbsent(type, seriesGenerator);

			XYChart.Data<Long, Number> entry = new XYChart.Data<>(monitoringTimestamp, monitorData.get(type));
			LOG.debug("Added {} with X: {}, Y:{}", type, monitoringTimestamp, monitorData.get(type));
			ObservableList<XYChart.Data<Long, Number>> data = series.getData();
			data.add(entry);
			cleanUpDataSet(data, limit);
			NumberAxis xAxis = (NumberAxis) (ValueAxis) chart.getXAxis();
			xAxis.setLowerBound(data.get(0).getXValue());
			xAxis.setUpperBound(data.get(data.size()-1).getXValue());
			updateTickUnitIfNeeded(cutOffUnit, cutOffValue, xAxis);
		}
		addEvents(DataType.EVENT, monitorData.getEvents(), limit);
	}

	private void updateTickUnitIfNeeded(TemporalUnit cutOffUnit, int cutOffValue, NumberAxis xAxis) {
		if (!cutOffUnit.equals(cutOffUnitOld) || cutOffValue != cutOffValueOld) {
			cutOffUnitOld = cutOffUnit;
			cutOffValueOld =cutOffValue;
			long newTickUnit = cutOffUnit.getDuration().multipliedBy(cutOffValue).dividedBy(NB_OF_TICKS).toMillis();
			xAxis.setTickUnit(newTickUnit);
		}
	}

	private void addEvents(DataType type, List<EventData> eventDatas, long limit) {
		XYChart.Series<Long, Number> series = seriesMap.computeIfAbsent(type, seriesGenerator);
		for (EventData eventData : eventDatas) {
			XYChart.Data<Long, Number> entry = new XYChart.Data<>(eventData.getTimestamp(), eventData.getValue());
			ObservableList<XYChart.Data<Long, Number>> data = series.getData();
			data.add(entry);
			LOG.debug("Added {} with X: {}, Y:{}", DataType.EVENT, eventData.getTimestamp(), eventData.getValue());
		}
		cleanUpDataSet(series.getData(), limit);
	}

	private void cleanUpDataSet(ObservableList<XYChart.Data<Long, Number>> data, long limit){
		data.removeIf(i -> i.getXValue() < limit);
	}

}
