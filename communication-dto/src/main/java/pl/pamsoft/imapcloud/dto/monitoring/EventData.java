package pl.pamsoft.imapcloud.dto.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class EventData {
	private long timestamp;
	private double value;

	public EventData(long timestamp, double value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public EventData() {
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
