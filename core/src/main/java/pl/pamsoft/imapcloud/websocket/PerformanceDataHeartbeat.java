package pl.pamsoft.imapcloud.websocket;

import org.apache.commons.lang.math.RandomUtils;
import org.bouncycastle.pqc.math.linearalgebra.RandUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

@Component
public class PerformanceDataHeartbeat {

	@Autowired
	private PerformanceDataService performanceDataService;

	@Scheduled(fixedRate = 1000)
	public void doIt() {

		PerformanceDataEvent performanceDataEvent = new PerformanceDataEvent(StatisticType.CHUNK_HASH, (long) RandomUtils.nextInt(100));
		performanceDataService.broadcast(performanceDataEvent);
	}
}
