package pl.pamsoft.imapcloud.websocket;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pamsoft.imapcloud.common.StatisticType;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
@AllArgsConstructor
@Data
// deserialized from json, so we need no args constructior
public class PerformanceDataEvent implements Event {
	private StatisticType type;
	private long currentValue;

	public PerformanceDataEvent(StatisticType type, Stopwatch stopwatch) {
		this.type = type;
		this.currentValue = stopwatch.elapsed(TimeUnit.MICROSECONDS);

	}
}
