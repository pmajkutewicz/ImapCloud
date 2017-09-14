package pl.pamsoft.imapcloud.integration;

import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

	public static Path createTempFile(long fileSize) throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");
		Path file = Paths.get(tempDir, RandomStringUtils.randomAlphabetic(10) + ".imc");
		MappedByteBuffer out =
			new RandomAccessFile(file.toFile(), "rw").getChannel()
				.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
		for (int i = 0; i < fileSize; i++) {
			out.put((byte) 'x');
		}
		return file;
	}
}
