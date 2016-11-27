package pl.pamsoft.imapcloud.services.upload;

import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.FilesIOService;

import java.util.List;
import java.util.function.Function;

public class DirectorySizeCalculator implements Function<List<FileDto>, Long> {

	private static final Logger LOG = LoggerFactory.getLogger(DirectorySizeCalculator.class);

	private FilesIOService filesIOService;
	private MonitoringHelper monitoringHelper;

	public DirectorySizeCalculator(FilesIOService filesIOService, MonitoringHelper monitoringHelper) {
		this.filesIOService = filesIOService;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public Long apply(List<FileDto> fileDtos) {
		long result = 0;
		Monitor monitor = monitoringHelper.start(Keys.UL_DIRECTORY_SIZE_CALC);
		for (FileDto fileDto : fileDtos) {
			if (FileDto.FileType.DIRECTORY == fileDto.getType()) {
				long dirSize = filesIOService.calculateDirSize(filesIOService.getFile(fileDto));
				LOG.debug("{} is {} bytes", fileDto.getAbsolutePath(), dirSize);
				result = +dirSize;
			} else {
				long fileSize = filesIOService.getFile(fileDto).length();
				LOG.debug("{} is {} bytes", fileDto.getAbsolutePath(), fileSize);
				result += fileSize;
			}
		}
		monitoringHelper.stop(monitor);
		return result;
	}

}
