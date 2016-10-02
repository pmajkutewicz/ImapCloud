package pl.pamsoft.imapcloud;

import pl.pamsoft.imapcloud.dto.FileDto;

import java.security.SecureRandom;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

	private static final Random RANDOM = new SecureRandom();

	public static byte[] getRandomBytes(int size) {
		byte[] in = new byte[size];
		RANDOM.nextBytes(in);
		return in;
	}

	public static FileDto mockFileDto() {
		FileDto fileDto = mock(FileDto.class);
		when(fileDto.getName()).thenReturn("exampleName");
		when(fileDto.getAbsolutePath()).thenReturn("/path/exampleName.txt");
		return fileDto;
	}
}
