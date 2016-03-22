package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DirectoryProcessor implements Function<UploadChunkContainer, Stream<UploadChunkContainer>> {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryProcessor.class);

	private FilesIOService filesService;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public DirectoryProcessor(FilesIOService filesService, Statistics statistics, PerformanceDataService performanceDataService) {
		this.filesService = filesService;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	public Stream<UploadChunkContainer> apply(UploadChunkContainer ucc) {
		LOG.debug("Parsing {}", ucc.getFileDto().getAbsolutePath());
		if (FileDto.FileType.DIRECTORY == ucc.getFileDto().getType()) {
			Stopwatch stopwatch = Stopwatch.createStarted();
			List<FileDto> dtos = parseDirectories(ucc.getFileDto());
			statistics.add(StatisticType.DIRECTORY_PARSER, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.DIRECTORY_PARSER, stopwatch));
			LOG.debug("Directory {} parsed in {}", ucc.getFileDto().getAbsolutePath(), stopwatch);
			return dtos.stream().map(file -> UploadChunkContainer.addFileDto(ucc, file));
		} else {
			return Stream.of(ucc.getFileDto()).map(file -> UploadChunkContainer.addFileDto(ucc, file));
		}
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	private List<FileDto> parseDirectories(FileDto fileDto) {
		List<FileDto> result = new ArrayList<>();
		for (FileDto dto : filesService.listFilesInDir(new File(fileDto.getAbsolutePath()))) {
			if (FileDto.FileType.DIRECTORY == dto.getType()) {
				result.addAll(parseDirectories(dto));
			} else {
				result.add(dto);
			}
		}
		return result;
	}
}
