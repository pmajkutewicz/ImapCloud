package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.File;
import java.util.List;
import java.util.function.Function;

public class DirectorySizeCalculator implements Function<List<FileDto>, Long> {

	private static final Logger LOG = LoggerFactory.getLogger(DirectorySizeCalculator.class);

	private FilesIOService filesIOService;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public DirectorySizeCalculator(FilesIOService filesIOService, Statistics statistics, PerformanceDataService performanceDataService) {
		this.filesIOService = filesIOService;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public Long apply(List<FileDto> fileDtos) {
		long result = 0;
		Stopwatch stopwatch = Stopwatch.createStarted();
		for (FileDto fileDto : fileDtos) {
			if (FileDto.Type.DIRECTORY == fileDto.getType()) {
				long dirSize = filesIOService.calculateDirSize(new File(fileDto.getAbsolutePath()));
				LOG.debug("{} is {} bytes", fileDto.getAbsolutePath(), dirSize);
				result = +dirSize;
			} else {
				long fileSize = new File(fileDto.getAbsolutePath()).length();
				LOG.debug("{} is {} bytes", fileDto.getAbsolutePath(), fileSize);
				result = +fileSize;
			}
		}
		statistics.add(StatisticType.DIRECTORY_SIZE_CALCULATOR, stopwatch.stop());
		performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.DIRECTORY_SIZE_CALCULATOR, stopwatch));
		return result;
	}

}
