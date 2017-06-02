package pl.pamsoft.imapcloud.config.jamon;

import com.jamonapi.MonKeyImp;
import org.apache.log4j.spi.LoggingEvent;

public class LogbackMonKey extends MonKeyImp {

	private static final long serialVersionUID = -6975990022212112024L;

	public LogbackMonKey(String summaryLabel, String detailLabel, String units, ch.qos.logback.classic.spi.LoggingEvent event) {
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
