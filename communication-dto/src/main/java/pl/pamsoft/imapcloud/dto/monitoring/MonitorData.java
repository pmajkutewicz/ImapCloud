package pl.pamsoft.imapcloud.dto.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class MonitorData {
	private double min, max, avg, total, hits;
	private MonitorDescription monitorDescription;
	private String monitorKey;
	private String units;
	private List<EventData> events;

	public MonitorData(double min, double max, double avg, double total, double hits, MonitorDescription monitorDescription, String monitorKey, String units, List<EventData> events) {
		this.min = min;
		this.max = max;
		this.avg = avg;
		this.total = total;
		this.hits = hits;
		this.monitorDescription = monitorDescription;
		this.monitorKey = monitorKey;
		this.units = units;
		this.events = events;
	}

	public MonitorData() {
	}

	public double getMin() {
		return this.min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return this.max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getAvg() {
		return this.avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getHits() {
		return this.hits;
	}

	public void setHits(double hits) {
		this.hits = hits;
	}

	public MonitorDescription getMonitorDescription() {
		return this.monitorDescription;
	}

	public void setMonitorDescription(MonitorDescription monitorDescription) {
		this.monitorDescription = monitorDescription;
	}

	public String getMonitorKey() {
		return this.monitorKey;
	}

	public void setMonitorKey(String monitorKey) {
		this.monitorKey = monitorKey;
	}

	public String getUnits() {
		return this.units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public List<EventData> getEvents() {
		return this.events;
	}

	public void setEvents(List<EventData> events) {
		this.events = events;
	}

	public double get(DataType type) {
		switch (type) {
			case MIN:
				return getMin();
			case MAX:
				return getMax();
			case AVG:
				return getAvg();
			default:
				throw new RuntimeException("Argh!");
		}
	}
}
