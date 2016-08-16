package pl.pamsoft.imapcloud.monitoring;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class MonHelper {
	public static final MonKey EXECUTOR_ACTIVE = new MonKeyImp("pl.pamsoft.imapcloud.services.AbstractBackgroundService.active", "thread");
	public static final MonKey EXECUTOR_QUEUE = new MonKeyImp("pl.pamsoft.imapcloud.services.AbstractBackgroundService.queue", "task");
	public static final MonKey IMAP_THROUGHPUT = new MonKeyImp("pl.pamsoft.imapcloud.imap.ChunkSaver.throughput", "bytes/s");

	public static Monitor add(MonKey key, double value) {
		return MonitorFactory.add(key, value);
	};

	public static Monitor get(Object obj) {
		return MonitorFactory.start(obj.getClass().getName());
	}

	public static double stop(Monitor monitor) {
		monitor.stop();
		return monitor.getLastValue();
	}
}
