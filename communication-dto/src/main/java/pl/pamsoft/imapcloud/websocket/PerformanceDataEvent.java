package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pamsoft.imapcloud.common.StatisticType;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
// deserialized from json, so we need no args constructior
public class PerformanceDataEvent {
	private StatisticType type;
	private double currentValue;
}
