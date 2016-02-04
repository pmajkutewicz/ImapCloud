package pl.pamsoft.imapcloud.services;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FileChunkIteratorTest {

	private static final int MEBIBYTE = 1024 * 1024;

	@Test
	public void fileReadingWithNotAlignedChunks() throws Exception {
		String filePath = getTempDir() + "/not_aligned.txt";
		writeFile(filePath, 10);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(3);
		fileChunkIterator.process(new File(filePath));

		assertThat(fileChunkIterator.next().capacity(), is(3));
		assertThat(fileChunkIterator.next().capacity(), is(3));
		assertThat(fileChunkIterator.next().capacity(), is(3));
		assertThat(fileChunkIterator.next().capacity(), is(1));
		deleteFile(filePath);
	}

	@Test
	public void fileShouldBeReadUsingConstantChunks() throws Exception {
		String filePath = getTempDir() + "/constant_chunks.txt";
		writeFile(filePath, MEBIBYTE);

		FileChunkIterator fileChunkIterator = new FileChunkIterator(1024);
		fileChunkIterator.process(new File(filePath));
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
		writeFile(filePath, MEBIBYTE);

		int deviation = 512;
		int fetchSize = 1024;
		FileChunkIterator fileChunkIterator = new FileChunkIterator(fetchSize, deviation);
		fileChunkIterator.process(new File(filePath));
		// last chunks can be shorter than deviation, so let say we have 20 tries
		for (int i = 0; i < 20; i++) {
			assertTrue(fileChunkIterator.hasNext());
			int capacity = fileChunkIterator.next().capacity();
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
