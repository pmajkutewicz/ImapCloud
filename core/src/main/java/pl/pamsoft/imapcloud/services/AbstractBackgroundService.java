package pl.pamsoft.imapcloud.services;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.entity.TaskProgress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressFBWarnings("PCOA_PARTIALLY_CONSTRUCTED_OBJECT_ACCESS")
abstract class AbstractBackgroundService {

	static final int DEFAULT_MAX_TASKS = 10;
	private static final int FIVETEEN = 15;

	private ExecutorService executor = Executors.newFixedThreadPool(getMaxTasks(), new ThreadFactoryBuilder().setNameFormat(getNameFormat()).setDaemon(false).build());
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	private Map<String, Future<?>> taskMap = new ConcurrentHashMap<>();
	private Map<String, TaskProgress> taskProgressMap = new ConcurrentHashMap<>();

	private Callable<Void> cleanUpTask = () -> {
		getTaskMap().entrySet()
			.stream()
			.filter(taskEntry -> taskEntry.getValue().isDone())
			.forEach(taskEntry -> {
					getTaskProgressMap().remove(taskEntry.getKey());
					getTaskMap().remove(taskEntry.getKey());
				}
			);
		return null;
	};


	@PostConstruct
	void init() {
		scheduledExecutorService.schedule(cleanUpTask, FIVETEEN, TimeUnit.MINUTES);
	}

	@PreDestroy
	void destroy() {
		executor.shutdown();
		scheduledExecutorService.shutdown();
	}

	ExecutorService getExecutor() {
		return executor;
	}

	Map<String, Future<?>> getTaskMap() {
		return taskMap;
	}

	Map<String, TaskProgress> getTaskProgressMap() {
		return taskProgressMap;
	}

	abstract int getMaxTasks();

	abstract String getNameFormat();
}
