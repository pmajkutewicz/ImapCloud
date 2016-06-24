package pl.pamsoft.imapcloud;

import pl.pamsoft.imapcloud.dto.FileDto;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

	public static byte[] getRandomBytes(int size) {
		byte[] in = new byte[size];
		new Random().nextBytes(in);
		return in;
	}

	public static FileDto mockFileDto() {
		FileDto fileDto = mock(FileDto.class);
		when(fileDto.getName()).thenReturn("exampleName");
		when(fileDto.getAbsolutePath()).thenReturn("/path/exampleName.txt");
		return fileDto;
	}
}
