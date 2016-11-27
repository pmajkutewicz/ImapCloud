package pl.pamsoft.imapcloud.services.download;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static pl.pamsoft.imapcloud.dto.FileDto.FileType.FILE;

public class FileSaverTest {

	private static final int FILE_SIZE = 1024;

	private FileSaver fileSaver;
	private String tempPath;

	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@BeforeClass
	public void init() throws NoSuchAlgorithmException {
		fileSaver = new FileSaver(monitoringHelper);
		tempPath = System.getProperty("java.io.tmpdir") + File.separator + "ic";
	}

	@Test
	public void shouldAppendToNewFile() throws IOException {
		String outputFileName = "out.txt";
		deleteTempFile(outputFileName);

		byte[] data = TestUtils.getRandomBytes(FILE_SIZE);
		FileChunk fileChunk = TestUtils.createFileChunk(outputFileName, false);
		FileDto fileDto = new FileDto(outputFileName, tempPath, FILE, (long) FILE_SIZE);

		DownloadChunkContainer dcc = new DownloadChunkContainer("irrelevant", fileChunk, fileDto);
		dcc = DownloadChunkContainer.addData(dcc, data);

		DownloadChunkContainer result = fileSaver.apply(dcc);

		assertEquals(result, dcc);
		assertEquals(Paths.get(tempPath, outputFileName).toFile().length(), FILE_SIZE);
		deleteTempFile(outputFileName);
	}

	@Test
	public void shouldAppendToExistingFile() throws IOException {
		String outputFileName = "out_existing.txt";
		deleteTempFile(outputFileName);

		byte[] data = TestUtils.getRandomBytes(FILE_SIZE);
		FileChunk fileChunk = TestUtils.createFileChunk(outputFileName, false);
		FileDto fileDto = new FileDto(outputFileName, tempPath, FILE, (long) FILE_SIZE);

		DownloadChunkContainer dcc = new DownloadChunkContainer("irrelevant", fileChunk, fileDto);
		dcc = DownloadChunkContainer.addData(dcc, data);

		fileSaver.apply(dcc);
		DownloadChunkContainer result = fileSaver.apply(dcc);

		assertEquals(result, dcc);
		assertEquals(Paths.get(tempPath, outputFileName).toFile().length(), FILE_SIZE * 2);
		deleteTempFile(outputFileName);
	}

	private void deleteTempFile(String filename) throws IOException {
		Path tempFile = Paths.get(tempPath, filename);
		if (Files.exists(tempFile)) {
			Files.delete(tempFile);
		}
		assertTrue(Files.notExists(tempFile));
	}
}
