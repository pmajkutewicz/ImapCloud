package pl.pamsoft.imapcloud.services.upload;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class DirectoryProcessorTest {

	private DirectoryProcessor directoryProcessor;

	private FilesIOService filesService = mock(FilesIOService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@BeforeClass
	public void init(){
		directoryProcessor = new DirectoryProcessor(filesService, monitoringHelper);
	}

	@Test
	public void shouldParseSingleFile() {
		FileDto fileDto = create("example");
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);

		Stream<UploadChunkContainer> result = directoryProcessor.apply(ucc);

		assertEquals(result.findFirst().get().getFileDto(), fileDto);
	}

	@Test
	public void shouldParseDirectoryFile() {
		FileDto fileDto = createDir();
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);

		Stream<UploadChunkContainer> result = directoryProcessor.apply(ucc);

		assertEquals(result.count(), 3);
	}

	private FileDto createDir() {
		File file = mock(File.class);
		FileDto dir = mock(FileDto.class);
		when(dir.getType()).thenReturn(FileDto.FileType.DIRECTORY);
		when(filesService.getFile(dir)).thenReturn(file);
		when(filesService.listFilesInDir(eq(file))).thenReturn(Arrays.asList(create("1"), create("2"), create("3")));
		return dir;
	}

	private FileDto create(String name) {
		FileDto fileDto = new FileDto();
		fileDto.setType(FileDto.FileType.FILE);
		fileDto.setName(name);
		fileDto.setAbsolutePath("/path/example.txt");
		return fileDto;
	}
}
