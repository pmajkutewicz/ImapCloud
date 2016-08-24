package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.pamsoft.imapcloud.dto.monitoring.MonitorData;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class MonitoringResponse extends AbstractResponse {
	private long monitoringTimestamp;
	private List<MonitorData> monitorDatas;
}
