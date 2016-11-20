package pl.pamsoft.imapcloud.services.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamonapi.Monitor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MonitoringServiceTest {

	private MonitoringService monitoringService = new MonitoringService();
	private MonitoringHelper monitoringHelper = new MonitoringHelper();

	@BeforeClass
	public void init() {
		monitoringHelper.initBuffers();
		monitoringService.setMonitoringHelper(monitoringHelper);
	}

	@Test
	public void shouldReturnAllMonitors() throws IOException {
		monitoringHelper.add(Keys.EXECUTOR_ACTIVE, 1);
		monitoringHelper.add(Keys.EXECUTOR_QUEUE, 3);

		List<Monitor> allMonitors = monitoringService.getAllMonitors();

		assertTrue(allMonitors.size() >= 2);
	}

	@Test
	public void shouldSerializeToJsonAndBack() throws IOException {
		monitoringHelper.add(Keys.IMAP_THROUGHPUT, 1000);
		monitoringHelper.add(Keys.IMAP_THROUGHPUT, 2000);
		Monitor monitor = monitoringHelper.add(Keys.IMAP_THROUGHPUT, 3000);
		String serialized = new ObjectMapper().writeValueAsString(monitor);
		JsonNode jsonNode = new ObjectMapper().readTree(serialized);

		assertEquals(jsonNode.get("avg").asDouble(), 2000D);
		assertEquals(jsonNode.get("lastValue").asDouble(), 3000D);
		assertEquals(jsonNode.get("hits").asInt(), 3);
	}
}
