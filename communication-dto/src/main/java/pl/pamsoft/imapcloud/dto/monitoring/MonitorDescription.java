package pl.pamsoft.imapcloud.dto.monitoring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.websocket.TaskType;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class MonitorDescription {

	public static final MonitorDescription OTHER = new MonitorDescription(null, null);

	private StatisticType statisticType;
	private TaskType taskType;

	public MonitorDescription() {
	}

	public MonitorDescription(StatisticType statisticType, TaskType taskType) {
		this.statisticType = statisticType;
		this.taskType = taskType;
	}

	public static MonitorDescription desc(StatisticType statisticType, TaskType taskType) {
		return new MonitorDescription(statisticType, taskType);
	}

	public StatisticType getStatisticType() {
		return statisticType;
	}

	public TaskType getTaskType() {
		return taskType;
	}

}
