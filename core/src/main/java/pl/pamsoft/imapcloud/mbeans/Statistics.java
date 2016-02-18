package pl.pamsoft.imapcloud.mbeans;

import com.google.common.base.Stopwatch;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@ManagedResource
public class Statistics {

	private static final int MAX_ENTRIES = 1000;
	private Map<StatisticType, LinkedList<Long>> content = new EnumMap<>(StatisticType.class);

	@PostConstruct
	public void init() {
		for (StatisticType statisticType : StatisticType.values()) {
			content.put(statisticType, new LinkedList<>());
		}
	}

	public void add(StatisticType type, Stopwatch value) {
		add(type, value.elapsed(TimeUnit.MICROSECONDS));
	}

	public void add(StatisticType type, long value) {
		LinkedList<Long> integers = content.get(type);
		integers.add(value);
		if (integers.size() > MAX_ENTRIES) {
			integers.removeFirst();
		}
	}

	private StatsContainer getStats(StatisticType type) {
		LinkedList<Long> values = content.get(type);
		long min = Integer.MAX_VALUE;
		long max = Integer.MIN_VALUE;
		long sum = 0;
		for (Long val : values) {
			sum = +val;
			if (val < min) {
				min = val;
			}
			if (val > max) {
				max = val;
			}
		}
		return new StatsContainer(min, max, sum / (double) values.size());
	}

	public long getMax(StatisticType type) {
		return getStats(type).getMax();
	}

	public long getMin(StatisticType type) {
		return getStats(type).getMin();
	}

	public double getAvg(StatisticType type) {
		return getStats(type).getAvg();
	}

	@ManagedAttribute
	public String getDirectoryParserStats() {
		return getStats(StatisticType.DIRECTORY_PARSER).toString();
	}

	@ManagedAttribute
	public String getFileHashStats() {
		return getStats(StatisticType.FILE_HASH).toString();
	}

	@ManagedAttribute
	public String getFileChunkCreatorStats() {
		return getStats(StatisticType.FILE_CHUNK_CREATOR).toString();
	}

	@ManagedAttribute
	public String getChunkHashStats() {
		return getStats(StatisticType.CHUNK_HASH).toString();
	}

	@ManagedAttribute
	public String getChunkEncoderStats() {
		return getStats(StatisticType.CHUNK_ENCODER).toString();
	}

	@ManagedAttribute
	public String getChunkSaverStats() {
		return getStats(StatisticType.CHUNK_SAVER).toString();
	}
}
