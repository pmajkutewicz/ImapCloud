package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.common.StatisticType;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
// deserialized from json, so we need no args constructior
public class PerformanceDataEvent {
	private StatisticType type;
	private double currentValue;

	public PerformanceDataEvent(StatisticType type, double currentValue) {
		this.type = type;
		this.currentValue = currentValue;
	}

	public PerformanceDataEvent() {
	}

	public StatisticType getType() {
		return this.type;
	}

	public void setType(StatisticType type) {
		this.type = type;
	}

	public double getCurrentValue() {
		return this.currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}
}
