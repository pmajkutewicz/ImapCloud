package pl.pamsoft.imapcloud.services.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamonapi.Monitor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonitoringServiceTest {

	private MonitoringService monitoringService = new MonitoringService();
	private MonitoringHelper monitoringHelper = new MonitoringHelper();

	@BeforeAll
	void init() {
		monitoringHelper.initBuffers();
		monitoringService.setMonitoringHelper(monitoringHelper);
	}

	@Test
	void shouldReturnAllMonitors() throws IOException {
		monitoringHelper.add(Keys.EXECUTOR_ACTIVE, 1);
		monitoringHelper.add(Keys.EXECUTOR_QUEUE, 3);

		List<Monitor> allMonitors = monitoringService.getAllMonitors();

		assertTrue(allMonitors.size() >= 2);
	}

	@Test
	void shouldSerializeToJsonAndBack() throws IOException {
		monitoringHelper.add(Keys.IMAP_THROUGHPUT, 1000);
		monitoringHelper.add(Keys.IMAP_THROUGHPUT, 2000);
		Monitor monitor = monitoringHelper.add(Keys.IMAP_THROUGHPUT, 3000);
		String serialized = new ObjectMapper().writeValueAsString(monitor);
		JsonNode jsonNode = new ObjectMapper().readTree(serialized);

		assertEquals(2000D, jsonNode.get("avg").asDouble());
		assertEquals(3000D, jsonNode.get("lastValue").asDouble());
		assertEquals(3, jsonNode.get("hits").asInt());
	}
}
