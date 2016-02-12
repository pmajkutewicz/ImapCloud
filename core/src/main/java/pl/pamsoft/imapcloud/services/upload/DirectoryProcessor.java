package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DirectoryProcessor implements Function<FileDto, Stream<FileDto>> {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryProcessor.class);

	private FilesService filesService;

	public DirectoryProcessor(FilesService filesService) {
		this.filesService = filesService;
	}

	@Override
	public Stream<FileDto> apply(FileDto fileDto) {
		if (FileDto.Type.DIRECTORY == fileDto.getType()) {
			Stopwatch stopwatch = Stopwatch.createStarted();
			List<FileDto> dtos = parseDirectories(fileDto);
			LOG.debug("Directory {} parsed in {}", fileDto.getAbsolutePath(), stopwatch.stop());
			return dtos.stream();
		} else {
			return Stream.of(fileDto);
		}
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	private List<FileDto> parseDirectories(FileDto fileDto) {
		List<FileDto> result = new ArrayList<>();
		for (FileDto dto : filesService.listFilesInDir(new File(fileDto.getAbsolutePath()))) {
			if (FileDto.Type.DIRECTORY == dto.getType()) {
				result.addAll(parseDirectories(dto));
			} else {
				result.add(dto);
			}
		}
		return result;
	}
}
