package pl.pamsoft.imapcloud.services.upload;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.FilesIOService;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static pl.pamsoft.imapcloud.dto.FileDto.FileType.FILE;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorySizeCalculatorTest {

	private DirectorySizeCalculator directorySizeCalculator;

	private FilesIOService filesIOService = mock(FilesIOService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@BeforeAll
	void init() {
		reset(filesIOService);
		directorySizeCalculator = new DirectorySizeCalculator(filesIOService, monitoringHelper);
		when(filesIOService.calculateDirSize(any(File.class))).thenCallRealMethod();
	}

	@Test
	void shouldCalculateFilesSize() {
		List<FileDto> files = Arrays.asList(create("a", FILE, 1024L), create("b", FILE, 2048L));
		when(filesIOService.getFile(any(FileDto.class))).thenAnswer(invocation -> {
            Object[] arguments = invocation.getArguments();
            FileDto argument = (FileDto) arguments[0];
            File f = mock(File.class);
            when(f.length()).thenReturn(argument.getSize());
            return f;
        });

		long size = directorySizeCalculator.apply(files);

		assertEquals(1024L + 2048L, size);
	}


	private FileDto create(String path, FileDto.FileType type, long size) {
		return new FileDto("irrelevant", path, type, size);
	}

}
