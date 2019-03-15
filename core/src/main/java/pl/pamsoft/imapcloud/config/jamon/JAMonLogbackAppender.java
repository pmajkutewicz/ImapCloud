package pl.pamsoft.imapcloud.config.jamon;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.jamonapi.JAMonListenerFactory;
import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.jamonapi.log4j.Log4jBufferListener;
import com.jamonapi.utils.DefaultGeneralizer;
import com.jamonapi.utils.Generalizer;

public class JAMonLogbackAppender extends AppenderBase<LoggingEvent> {
	static {
		// Register this object to be available for use in the
		// JAMonListenerFactory.
		JAMonListenerFactory.put(new Log4jBufferListener());
	}

	/* Prefix for this classes jamon monitor labels */
	private static final String PREFIX = "com.jamonapi.log4j.JAMonAppender.";
	// any of these poperties can be overridden via log4j configurators.
	// CSOFF: MagicNumber
	private int bufferSize = 100;
	// CSON: MagicNumber
	private String units = "log4j"; // units in jamon montiors
	// indicates whether or not log4j LoggingEvent info is placed in buffer.
	// This could potentially be slower though I didn't test it, and I
	// wouldn't be overly concerned about it.
	private boolean enableListenerDetails = true;
	// Enable monitoring of the various log4j levels in jamon.
	private boolean enableLevelMonitoring = true;
	private boolean generalize = false;
	private Generalizer generalizer = new DefaultGeneralizer();

	@Override
	protected void append(LoggingEvent event) {
		String message = event.getFormattedMessage();
		if (isEnableLevelMonitoring()) {
			// monitor that counts all calls to log4j logging methods
			MonitorFactory.add(createKey(PREFIX + "TOTAL", message, event), 1);
			// monitor that counts calls to log4j at each level (DEBUG/WARN/...)
			MonitorFactory.add(createKey(PREFIX + event.getLevel(), message, event), 1);
		}

		// if the object was configured to generalize the message then do as
		// such. This will create a jamon record with the generalized method
		// so it is important for the developer to ensure that the generalized
		// message is unique enough not to grow jamon unbounded.
		if (isGeneralize()) {
			MonitorFactory.add(createKey(generalize(message), message, event), 1);
		}
	}

	private MonKey createKey(String summaryLabel, String detailLabel, LoggingEvent event) {
		if (enableListenerDetails) {// put array in details buffer
			return new LogbackMonKey(summaryLabel, detailLabel, units, event);
		} else {
			return new MonKeyImp(summaryLabel, detailLabel, units);
		}
	}

	/**
	 * generalize the passed in String if a Genaralizer is set
	 */
	private String generalize(String detailedMessage) {
		return (generalizer != null) ? generalizer.generalize(detailedMessage) : detailedMessage;
	}

	/**
	 * Note this is primarily used by the log4j configurator. Valid values are
	 * the various log4j levels:
	 *
	 * <ul>
	 * <li>DEBUG/ERROR/WARN/INFO/ERROR/FATAL, as well as...
	 * <li>TOTAL (AccountAspect listener that gets called for all levels),
	 * <li>BASIC (same as calling TOTAL/ERROR/FATAL),
	 * <li>ALL (same as calling ERROR/WARN/INFO/ERROR/FATAL/TOTAL).
	 * </ul>
	 *
	 * <p>Note: Values are not case sensitive.
	 *
	 * @param level
	 */
	public void setEnableListeners(String level) {
		if (Level.TRACE.toString().equalsIgnoreCase(level.toUpperCase())) {
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.TRACE, units));
		} else if (Level.DEBUG.toString().equalsIgnoreCase(level.toUpperCase())) {
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.DEBUG, units));
		} else if (Level.INFO.toString().equalsIgnoreCase(level.toUpperCase())) {
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.INFO, units));
		} else if (Level.WARN.toString().equalsIgnoreCase(level.toUpperCase())) {
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.WARN, units));
		} else if (Level.ERROR.toString().equalsIgnoreCase(level.toUpperCase())) {
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.ERROR, units));
		} else if ("TOTAL".equalsIgnoreCase(level.toUpperCase())) {
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + "TOTAL", units));
		} else if (Level.ALL.toString().equalsIgnoreCase(level.toUpperCase())) {
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.TRACE, units));
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.DEBUG, units));
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.INFO, units));
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.WARN, units));
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + Level.ERROR, units));
			addDefaultListener(MonitorFactory.getMonitor(PREFIX + "TOTAL", units));
		}
	}

	// Add a Log4jBufferListener to the passed in Monitor
	private void addDefaultListener(Monitor mon) {
		if (!mon.hasListeners()) {
			Log4jBufferListener listener = new Log4jBufferListener();
			listener.getBufferList().setBufferSize(bufferSize);
			mon.addListener("value", listener);
		}
	}

	// region getters&setters
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public boolean isEnableListenerDetails() {
		return enableListenerDetails;
	}

	public void setEnableListenerDetails(boolean enableListenerDetails) {
		this.enableListenerDetails = enableListenerDetails;
	}

	public boolean isEnableLevelMonitoring() {
		return enableLevelMonitoring;
	}

	public void setEnableLevelMonitoring(boolean enableLevelMonitoring) {
		this.enableLevelMonitoring = enableLevelMonitoring;
	}

	public boolean isGeneralize() {
		return generalize;
	}

	public void setGeneralize(boolean generalize) {
		this.generalize = generalize;
	}

	public Generalizer getGeneralizer() {
		return generalizer;
	}

	public void setGeneralizer(Generalizer generalizer) {
		this.generalizer = generalizer;
	}
	// endregion
}
