package pl.pamsoft.imapcloud.dto.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class EventData {
	private long timestamp;
	private double value;
}
