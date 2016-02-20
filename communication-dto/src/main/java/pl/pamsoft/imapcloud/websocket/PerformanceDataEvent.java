package pl.pamsoft.imapcloud.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.pamsoft.imapcloud.common.StatisticType;

import javax.annotation.concurrent.Immutable;

@Immutable
@RequiredArgsConstructor
@Getter
public class PerformanceDataEvent implements Event {
	private final StatisticType type;
	private final Long currentValue;
}
