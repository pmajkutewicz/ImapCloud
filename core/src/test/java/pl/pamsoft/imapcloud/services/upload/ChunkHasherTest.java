package pl.pamsoft.imapcloud.services.upload;

import org.junit.Test;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.security.MessageDigest;

import static org.junit.Assert.*;

public class ChunkHasherTest {

	@Test
	public void shouldCalculateSha512Hash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);

		//when
		ChunkHasher chunkHasher = new ChunkHasher(MessageDigest.getInstance("SHA-512"));
		UploadChunkContainer result = chunkHasher.apply(new UploadChunkContainer(mockedFileDto, test.getBytes(), 1));

		//then
		assertEquals("911373ca94482a9f0ef1c23a0c0abbd166f1540e6da544bb920dba59377051874b8d8c93014c51c764bd754185cb9ca3fcf9adfbcfcb84b0df8aa856dd5d4209", result.getChunkHash());
	}

	@Test
	public void shouldCalculateMD5Hash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);

		//when
		ChunkHasher chunkHasher = new ChunkHasher(MessageDigest.getInstance("MD5"));
		UploadChunkContainer result = chunkHasher.apply(new UploadChunkContainer(mockedFileDto, test.getBytes(), 1));

		//then
		assertEquals("3a760fae784d30a1b50e304e97a17355", result.getChunkHash());
	}
}
