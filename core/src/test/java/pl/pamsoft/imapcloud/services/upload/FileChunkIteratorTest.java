package pl.pamsoft.imapcloud.services.upload;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FileChunkIteratorTest {

	private static final int MEBIBYTE = 1024 * 1024;

	private PerformanceDataService performanceDataService = Mockito.mock(PerformanceDataService.class);
	private MonitoringHelper monitoringHelper = Mockito.mock(MonitoringHelper.class);

	@DataProvider
	public Object[][] lastChunkShouldBeMarkedAsLastDataProvider() {
		return new Object[][] {
			{10, 1, 9},
			{10, 3, 3}
		};
	}

	@Test(dataProvider = "lastChunkShouldBeMarkedAsLastDataProvider")
	public void lastChunkShouldBeMarkedAsLast(int fileSize, int readSize, int nbOfNonLastChunks) throws IOException {
		String filePath = getTempDir() + "/last_chunk.txt";
		FileDto mockedFileDto = createFile(filePath, fileSize);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), readSize, performanceDataService, monitoringHelper);
		fileChunkIterator.process();

		for (int i = 0; i < nbOfNonLastChunks; i++) {
			assertFalse(fileChunkIterator.next().isLastChunk());
		}
		assertTrue(fileChunkIterator.next().isLastChunk());
	}

	@Test
	public void fileReadingWithNotAlignedChunks() throws IOException {
		String filePath = getTempDir() + "/not_aligned.txt";
		FileDto mockedFileDto = createFile(filePath, 10);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), 3, performanceDataService, monitoringHelper);
		fileChunkIterator.process();

		assertEquals(3, fileChunkIterator.next().getData().length);
		assertEquals(3, fileChunkIterator.next().getData().length);
		assertEquals(3, fileChunkIterator.next().getData().length);
		assertEquals(1, fileChunkIterator.next().getData().length);
		deleteFile(filePath);
	}

	@Test
	public void fileShouldBeReadUsingConstantChunks() throws IOException {
		String filePath = getTempDir() + "/constant_chunks.txt";
		FileDto mockedFileDto = createFile(filePath, MEBIBYTE);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), 1024, performanceDataService, monitoringHelper);
		fileChunkIterator.process();
		int counter = 0;
		while (fileChunkIterator.hasNext()) {
			fileChunkIterator.next();
			counter++;
		}
		//There was 1024 chunks
		assertEquals(1024, counter);
		deleteFile(filePath);
	}

	@Test
	public void fileShouldBeReadUsingVariableChunks() throws IOException {
		String filePath = getTempDir() + "/variable_chunks.txt";
		FileDto mockedFileDto = createFile(filePath, MEBIBYTE);

		int deviation = 512;
		int fetchSize = 1024;
		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), fetchSize, deviation, performanceDataService, monitoringHelper);
		fileChunkIterator.process();
		// last chunks can be shorter than deviation, so let say we have 20 tries
		for (int i = 0; i < 20; i++) {
			assertTrue(fileChunkIterator.hasNext());
			int capacity = fileChunkIterator.next().getData().length;
			assertTrue(capacity > fetchSize - deviation && capacity < fetchSize + deviation);
		}
		deleteFile(filePath);
	}

	private FileDto createFile(String filePath, int bytes) throws IOException {
		FileDto mockedFileDto = Mockito.mock(FileDto.class);
		when(mockedFileDto.getAbsolutePath()).thenReturn(filePath);
		writeFile(filePath, bytes);
		return mockedFileDto;
	}

	private void writeFile(String path, int bytes) throws IOException {
		OutputStream outputStream = null;
		File f = null;
		try {
			f = new File(path);
			f.createNewFile();
			byte[] buf = new byte[bytes];
			outputStream = new BufferedOutputStream(new FileOutputStream(f));
			outputStream.write(buf);
			outputStream.flush();
		} finally {
			try {
				outputStream.close();
			} catch (IOException ignored) {
			}
		}
	}

	private void deleteFile(String filePath) {
		new File(filePath).delete();
	}

	private String getTempDir() {
		File file = new File("/run/shm/");
		if (file.isDirectory() && file.exists()) {
			return "/run/shm/";
		}
		return System.getProperty("java.io.tmpdir");
	}
}
