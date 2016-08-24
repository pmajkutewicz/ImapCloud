package pl.pamsoft.imapcloud.services.upload;

import com.jamonapi.Monitor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DirectoryProcessor implements Function<UploadChunkContainer, Stream<UploadChunkContainer>> {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryProcessor.class);

	private FilesIOService filesService;
	private PerformanceDataService performanceDataService;
	private MonitoringHelper monitoringHelper;

	public DirectoryProcessor(FilesIOService filesService, PerformanceDataService performanceDataService, MonitoringHelper monitoringHelper) {
		this.filesService = filesService;
		this.performanceDataService = performanceDataService;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public Stream<UploadChunkContainer> apply(UploadChunkContainer ucc) {
		LOG.debug("Parsing {}", ucc.getFileDto().getAbsolutePath());
		if (FileDto.FileType.DIRECTORY == ucc.getFileDto().getType()) {
			Monitor monitor = monitoringHelper.start(Keys.UL_DIRECTORY_PROCESSOR);
			List<FileDto> dtos = parseDirectories(ucc.getFileDto());
			double lastVal = monitoringHelper.stop(monitor);
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.DIRECTORY_PROCESSOR, lastVal));
			LOG.debug("Directory {} parsed in {}", ucc.getFileDto().getAbsolutePath(), lastVal);
			return dtos.stream().map(file -> UploadChunkContainer.addFileDto(ucc, file));
		} else {
			return Stream.of(ucc.getFileDto()).map(file -> UploadChunkContainer.addFileDto(ucc, file));
		}
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	private List<FileDto> parseDirectories(FileDto fileDto) {
		List<FileDto> result = new ArrayList<>();
		for (FileDto dto : filesService.listFilesInDir(filesService.getFile(fileDto))) {
			if (FileDto.FileType.DIRECTORY == dto.getType()) {
				result.addAll(parseDirectories(dto));
			} else {
				result.add(dto);
			}
		}
		return result;
	}
}
