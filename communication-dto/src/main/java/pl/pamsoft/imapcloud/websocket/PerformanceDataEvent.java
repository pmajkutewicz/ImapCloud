package pl.pamsoft.imapcloud.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.pamsoft.imapcloud.common.StatisticType;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PerformanceDataEvent implements Event {
	private StatisticType type;
	private long currentValue;
}
