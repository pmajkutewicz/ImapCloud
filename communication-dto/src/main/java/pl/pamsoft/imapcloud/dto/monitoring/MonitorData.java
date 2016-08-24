package pl.pamsoft.imapcloud.dto.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class MonitorData {
	private double min, max, avg, total, hits;
	private MonitorDescription monitorDescription;
	private String monitorLabel;
	private String units;
	private List<EventData> events;

}
