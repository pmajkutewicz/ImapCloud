package pl.pamsoft.imapcloud.services.upload;

import org.mockito.Mockito;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class FileChunkIteratorTest {

	private static final int MEBIBYTE = 1024 * 1024;

	private Statistics statistics = mock(Statistics.class);
	private PerformanceDataService performanceDataService = Mockito.mock(PerformanceDataService.class);

	@Test
	public void fileReadingWithNotAlignedChunks() throws Exception {
		String filePath = getTempDir() + "/not_aligned.txt";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);
		when(mockedFileDto.getAbsolutePath()).thenReturn(filePath);
		writeFile(filePath, 10);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), 3, statistics, performanceDataService);
		fileChunkIterator.process();

		assertEquals(3, fileChunkIterator.next().getData().length);
		assertEquals(3, fileChunkIterator.next().getData().length);
		assertEquals(3, fileChunkIterator.next().getData().length);
		assertEquals(1, fileChunkIterator.next().getData().length);
		deleteFile(filePath);
	}

	@Test
	public void fileShouldBeReadUsingConstantChunks() throws Exception {
		String filePath = getTempDir() + "/constant_chunks.txt";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);
		when(mockedFileDto.getAbsolutePath()).thenReturn(filePath);
		writeFile(filePath, MEBIBYTE);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), 1024, statistics, performanceDataService);
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
	public void fileShouldBeReadUsingVariableChunks() throws Exception {
		String filePath = getTempDir() + "/variable_chunks.txt";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);
		when(mockedFileDto.getAbsolutePath()).thenReturn(filePath);
		writeFile(filePath, MEBIBYTE);


		int deviation = 512;
		int fetchSize = 1024;
		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), fetchSize, deviation, statistics, performanceDataService);
		fileChunkIterator.process();
		// last chunks can be shorter than deviation, so let say we have 20 tries
		for (int i = 0; i < 20; i++) {
			assertTrue(fileChunkIterator.hasNext());
			int capacity = fileChunkIterator.next().getData().length;
			assertTrue(capacity > fetchSize - deviation && capacity < fetchSize + deviation);
		}
		deleteFile(filePath);
	}

	private void writeFile(String path, int bytes) throws Exception {
		OutputStream outputStream = null;
		File f = null;
		try {
			f = new File(path);
			f.createNewFile();
			byte[] buf = new byte[bytes];
			outputStream = new BufferedOutputStream(new FileOutputStream(f));
			outputStream.write(buf);
			outputStream.flush();
		} catch (Exception e) {
			throw new Exception();
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
