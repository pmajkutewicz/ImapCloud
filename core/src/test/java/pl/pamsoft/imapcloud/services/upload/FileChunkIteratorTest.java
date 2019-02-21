package pl.pamsoft.imapcloud.services.upload;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.when;

class FileChunkIteratorTest {

	private static final int MEBIBYTE = 1024 * 1024;

	private MonitoringHelper monitoringHelper = Mockito.mock(MonitoringHelper.class);

	static Stream<Arguments> lastChunkShouldBeMarkedAsLastDataProvider() {
		return Stream.of(  //
			of(10, 1, 9), //
			of(10, 3, 3 //
			)
		);
	}

	@ParameterizedTest
	@MethodSource("lastChunkShouldBeMarkedAsLastDataProvider")
	void lastChunkShouldBeMarkedAsLast(int fileSize, int readSize, int nbOfNonLastChunks) throws IOException {
		String filePath = getTempDir() + "/last_chunk.txt";
		FileDto mockedFileDto = createFile(filePath, fileSize);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), readSize, monitoringHelper);
		fileChunkIterator.process();

		for (int i = 0; i < nbOfNonLastChunks; i++) {
			assertFalse(fileChunkIterator.next().isLastChunk());
		}
		assertTrue(fileChunkIterator.next().isLastChunk());
	}

	@Test
	void fileReadingWithNotAlignedChunks() throws IOException {
		String filePath = getTempDir() + "/not_aligned.txt";
		FileDto mockedFileDto = createFile(filePath, 10);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), 3, monitoringHelper);
		fileChunkIterator.process();

		assertEquals(fileChunkIterator.next().getData().length, 3);
		assertEquals(fileChunkIterator.next().getData().length, 3);
		assertEquals(fileChunkIterator.next().getData().length, 3);
		assertEquals(fileChunkIterator.next().getData().length, 1);
		deleteFile(filePath);
	}

	@Test
	void fileShouldBeReadUsingConstantChunks() throws IOException {
		String filePath = getTempDir() + "/constant_chunks.txt";
		FileDto mockedFileDto = createFile(filePath, MEBIBYTE);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), 1024, monitoringHelper);
		fileChunkIterator.process();
		int counter = 0;
		while (fileChunkIterator.hasNext()) {
			fileChunkIterator.next();
			counter++;
		}
		//There was 1024 chunks
		assertEquals(counter, 1024);
		deleteFile(filePath);
	}

	@Test
	void fileShouldBeReadUsingVariableChunks() throws IOException {
		String filePath = getTempDir() + "/variable_chunks.txt";
		FileDto mockedFileDto = createFile(filePath, MEBIBYTE);

		int deviation = 512;
		int fetchSize = 1024;
		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer("testId", mockedFileDto), fetchSize, deviation, monitoringHelper);
		fileChunkIterator.process();
		while (fileChunkIterator.hasNext()) {
			UploadChunkContainer chunk = fileChunkIterator.next();
			int capacity = chunk.getData().length;
			if (!chunk.isLastChunk()) {
				assertTrue(capacity >= fetchSize - deviation && capacity <= fetchSize + deviation);
			}
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
