package pl.pamsoft.imapcloud.config.jamon;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.jamonapi.MonKeyImp;

public class LogbackMonKey extends MonKeyImp {

	private static final long serialVersionUID = -6975990022212112024L;

	public LogbackMonKey(String summaryLabel, String detailLabel, String units, LoggingEvent event) {
		super(summaryLabel, detailLabel, units);
		this.setParam(event);
	}

	public LoggingEvent getLoggingEvent() {
		return (LoggingEvent) this.getParam();
	}

	public Object getValue(String key) {
		return "LoggingEvent".equalsIgnoreCase(key) ? this.getParam() : super.getValue(key);
	}
}
