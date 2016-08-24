package pl.pamsoft.imapcloud.services.common;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class MonitoringService {

	private MonitoringHelper monitoringHelper;

	public List<Monitor> getAllMonitors() {
		return monitoringHelper.getAllKeys().stream().map(MonitorFactory::getMonitor).collect(toList());
	}

	@Autowired
	public void setMonitoringHelper(MonitoringHelper monitoringHelper) {
		this.monitoringHelper = monitoringHelper;
	}
}
