package pl.pamsoft.imapcloud.rest;

import pl.pamsoft.imapcloud.responses.MonitoringResponse;

public class MonitoringRestClient extends AbstractRestClient {

	private static final String GET_MONITORS = "monitoring";
	private static final String GET_MONITORS_AFTER = "monitoring/filter/events/after";

	public MonitoringRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public void getMonitors(RequestCallback<MonitoringResponse> callback) {
		sendGet(GET_MONITORS, MonitoringResponse.class, callback);
	}

	public void getMonitorsAfter(long timestamp, RequestCallback<MonitoringResponse> callback) {
		sendGet(GET_MONITORS_AFTER, MonitoringResponse.class,"timestamp", String.valueOf(timestamp), callback);
	}

}
