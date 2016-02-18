package pl.pamsoft.imapcloud.services.upload;

import org.junit.Test;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileChunkIteratorTest {

	private static final int MEBIBYTE = 1024 * 1024;

	private Statistics statistics = mock(Statistics.class);

	@Test
	public void fileReadingWithNotAlignedChunks() throws Exception {
		String filePath = getTempDir() + "/not_aligned.txt";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);
		when(mockedFileDto.getAbsolutePath()).thenReturn(filePath);
		writeFile(filePath, 10);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer(mockedFileDto), 3, statistics);
		fileChunkIterator.process();

		assertThat(fileChunkIterator.next().getData().length, is(3));
		assertThat(fileChunkIterator.next().getData().length, is(3));
		assertThat(fileChunkIterator.next().getData().length, is(3));
		assertThat(fileChunkIterator.next().getData().length, is(1));
		deleteFile(filePath);
	}

	@Test
	public void fileShouldBeReadUsingConstantChunks() throws Exception {
		String filePath = getTempDir() + "/constant_chunks.txt";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);
		when(mockedFileDto.getAbsolutePath()).thenReturn(filePath);
		writeFile(filePath, MEBIBYTE);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer(mockedFileDto), 1024, statistics);
		fileChunkIterator.process();
		int counter = 0;
		while (fileChunkIterator.hasNext()) {
			fileChunkIterator.next();
			counter++;
		}
		//There was 1024 chunks
		assertThat(counter, is(1024));
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
		FileChunkIterator fileChunkIterator = new FileChunkIterator(new UploadChunkContainer(mockedFileDto), fetchSize, deviation, statistics);
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
			} catch (IOException e) {
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
