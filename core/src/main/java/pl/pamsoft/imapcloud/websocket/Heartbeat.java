package pl.pamsoft.imapcloud.websocket;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.services.websocket.TasksProgressService;

@Component
public class Heartbeat {

	@Autowired
	private PerformanceDataService performanceDataService;

	@Autowired
	private TasksProgressService tasksProgressService;

	private long sum = 0;
	private TaskProgressEvent id = new TaskProgressEvent("id", Long.MAX_VALUE);

	@Scheduled(fixedRate = 1000)
	public void doIt() {
		long currentValue = (long) RandomUtils.nextInt(100);
		performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_HASH, currentValue));
		id.process(currentValue);
		tasksProgressService.broadcast(id);
	}
}
