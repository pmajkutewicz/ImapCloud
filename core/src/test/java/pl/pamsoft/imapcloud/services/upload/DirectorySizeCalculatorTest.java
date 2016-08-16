package pl.pamsoft.imapcloud.services.upload;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static pl.pamsoft.imapcloud.dto.FileDto.FileType.FILE;

public class DirectorySizeCalculatorTest {

	private DirectorySizeCalculator directorySizeCalculator;

	private FilesIOService filesIOService = mock(FilesIOService.class);
	private PerformanceDataService performanceDataService = mock(PerformanceDataService.class);

	@BeforeClass
	public void setup() {
		reset(filesIOService);
		directorySizeCalculator = new DirectorySizeCalculator(filesIOService, performanceDataService);
		when(filesIOService.calculateDirSize(any(File.class))).thenCallRealMethod();
	}

	@Test
	public void shouldCalculateFilesSize() {
		List<FileDto> files = Arrays.asList(create("a", FILE, 1024L), create("b", FILE, 2048L));
		when(filesIOService.getFile(any(FileDto.class))).thenAnswer(new Answer<File>() {
			@Override
			public File answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();
				FileDto argument = (FileDto) arguments[0];
				File f = mock(File.class);
				when(f.length()).thenReturn(argument.getSize());
				return f;
			}
		});

		long size = directorySizeCalculator.apply(files);

		assertEquals(size, 1024L + 2048L);
	}


	private FileDto create(String path, FileDto.FileType type, long size) {
		return new FileDto("irrelevant", path, type, size);
	}

}
