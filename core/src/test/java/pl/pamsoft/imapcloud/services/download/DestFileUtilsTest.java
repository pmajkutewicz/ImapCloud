package pl.pamsoft.imapcloud.services.download;

import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.nio.file.Path;

import static java.io.File.separator;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

public class DestFileUtilsTest {

	private static final String EXAMPLE_PATH = "/tmp";
	private static final String EXAMPLE_FILE_NAME = "file.txt";

	@Test
	public void shouldGenerateDirPath() {
		FileDto fileDto = new FileDto("irrelevant", EXAMPLE_PATH, FileDto.FileType.FILE, 123L);
		DownloadChunkContainer dcc = new DownloadChunkContainer("irrelevant", mock(FileChunk.class), fileDto);
		Path path = DestFileUtils.generateDirPath(dcc);
		assertEquals(path.toString(), EXAMPLE_PATH);
	}

	@Test
	public void shouldGenerateFilePath() {
		File ownerFile = new File();
		ownerFile.setName(EXAMPLE_FILE_NAME);
		FileDto fileDto = new FileDto("irrelevant", EXAMPLE_PATH, FileDto.FileType.FILE, 123L);
		FileChunk fc = new FileChunk();
		fc.setOwnerFile(ownerFile);
		DownloadChunkContainer dcc = new DownloadChunkContainer("irrelevant", fc, fileDto);
		Path path = DestFileUtils.generateFilePath(dcc);
		assertEquals(path.toString(), EXAMPLE_PATH + separator + EXAMPLE_FILE_NAME);
	}
}
