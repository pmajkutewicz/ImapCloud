package pl.pamsoft.imapcloud.services.upload;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.exceptions.ChunkAlreadyExistException;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileChunkStorerTest {

	private FileChunkStorer fileChunkStorer;

	private FileServices fileServices = mock(FileServices.class);

	@BeforeClass
	public void init() {
		fileChunkStorer = new FileChunkStorer(fileServices);
	}


	@Test
	public void shouldStoreChunk() throws ChunkAlreadyExistException {
		FileDto fileDto = mock(FileDto.class);
		when(fileDto.getName()).thenReturn("exampleName");
		when(fileDto.getAbsolutePath()).thenReturn("/path/exampleName.txt");
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);

		fileChunkStorer.apply(ucc);

		verify(fileServices, times(1)).saveChunk(ucc);
	}
}
