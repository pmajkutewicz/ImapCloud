package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.monitoring.MonitorData;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class MonitoringResponse extends AbstractResponse {
	private long monitoringTimestamp;
	private List<MonitorData> monitorDatas;

	public MonitoringResponse(long monitoringTimestamp, List<MonitorData> monitorDatas) {
		this.monitoringTimestamp = monitoringTimestamp;
		this.monitorDatas = monitorDatas;
	}

	public MonitoringResponse() {
	}

	public long getMonitoringTimestamp() {
		return this.monitoringTimestamp;
	}

	public void setMonitoringTimestamp(long monitoringTimestamp) {
		this.monitoringTimestamp = monitoringTimestamp;
	}

	public List<MonitorData> getMonitorDatas() {
		return this.monitorDatas;
	}

	public void setMonitorDatas(List<MonitorData> monitorDatas) {
		this.monitorDatas = monitorDatas;
	}
}
