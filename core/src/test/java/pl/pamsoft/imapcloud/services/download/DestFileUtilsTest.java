package pl.pamsoft.imapcloud.services.download;

import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.nio.file.Path;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DestFileUtilsTest {

	private static final String EXAMPLE_PATH = "/tmp";
	private static final String EXAMPLE_FILE_NAME = "file.txt";

	@Test
	void shouldGenerateDirPath() {
		FileDto fileDto = new FileDto("irrelevant", EXAMPLE_PATH, FileDto.FileType.FILE, 123L);
		DownloadChunkContainer dcc = new DownloadChunkContainer("irrelevant", mock(FileChunk.class), fileDto, "irrelevant", "irrelevant");
		Path path = DestFileUtils.generateDirPath(dcc);
		assertEquals(EXAMPLE_PATH, path.toString());
	}

	@Test
	void shouldGenerateFilePath() {
		FileDto fileDto = new FileDto("irrelevant", EXAMPLE_PATH, FileDto.FileType.FILE, 123L);
		FileChunk fc = TestUtils.createFileChunk(EXAMPLE_FILE_NAME, false);
		DownloadChunkContainer dcc = new DownloadChunkContainer("irrelevant", fc, fileDto, fc.getChunkHash(), fc.getOwnerFile().getFileHash());
		Path path = DestFileUtils.generateFilePath(dcc);
		assertEquals(EXAMPLE_PATH + separator + EXAMPLE_FILE_NAME, path.toString());
	}
}
