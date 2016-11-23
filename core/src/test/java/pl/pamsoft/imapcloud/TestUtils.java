package pl.pamsoft.imapcloud;

import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.security.SecureRandom;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

	public static final String EXAMPLE_FILE_HASH = "expectedFileHash";
	public static final String FILE_ID = "fileId";
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

	public static FileChunk createFileChunk(String fileName, boolean isLastChunk) {
		File file = new File();
		file.setFileUniqueId(FILE_ID);
		file.setFileHash(EXAMPLE_FILE_HASH);
		file.setName(fileName);
		FileChunk fc = new FileChunk();
		fc.setChunkNumber(5);
		fc.setOwnerFile(file);
		fc.setLastChunk(isLastChunk);
		return fc;
	}
}
