package pl.pamsoft.imapcloud.websocket;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pamsoft.imapcloud.common.StatisticType;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
// deserialized from json, so we need no args constructior
public class PerformanceDataEvent {
	private StatisticType type;
	private long currentValue;

	public PerformanceDataEvent(StatisticType type, Stopwatch stopwatch) {
		this.type = type;
		this.currentValue = stopwatch.elapsed(TimeUnit.MICROSECONDS);
	}
}
