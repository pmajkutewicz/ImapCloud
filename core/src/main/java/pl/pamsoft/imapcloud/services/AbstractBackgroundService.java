package pl.pamsoft.imapcloud.services;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.exceptions.IMAPCloudUncaughtExceptionHandler;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressFBWarnings("PCOA_PARTIALLY_CONSTRUCTED_OBJECT_ACCESS")
abstract class AbstractBackgroundService {

	protected static final int DEFAULT_MAX_TASKS = 10;
	private static final int FIVETEEN = 15;

	private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
		getMaxTasks(), new ThreadFactoryBuilder().setNameFormat(getNameFormat()).setDaemon(false).setUncaughtExceptionHandler(new IMAPCloudUncaughtExceptionHandler()).build());
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
	protected void init() {
		scheduledExecutorService.schedule(cleanUpTask, FIVETEEN, TimeUnit.MINUTES);
	}

	@PreDestroy
	protected void destroy() {
		executor.shutdown();
		scheduledExecutorService.shutdown();
	}

	protected Future<Void> runAsyncOnExecutor(Runnable runnable) {
		return CompletableFuture.runAsync(runnable, getExecutor()).exceptionally(new IMAPCloudUncaughtExceptionHandler());
	}

	protected ExecutorService getExecutor() {
		return executor;
	}

	protected Map<String, Future<?>> getTaskMap() {
		getMonitoringHelper().add(Keys.EXECUTOR_ACTIVE, executor.getActiveCount());
		getMonitoringHelper().add(Keys.EXECUTOR_QUEUE, executor.getQueue().size());
		return taskMap;
	}

	protected Map<String, TaskProgress> getTaskProgressMap() {
		return taskProgressMap;
	}

	protected abstract int getMaxTasks();

	protected abstract String getNameFormat();

	protected abstract MonitoringHelper getMonitoringHelper();
}
