package pl.pamsoft.imapcloud.services.recovery;

import org.apache.commons.vfs2.FileSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.containers.RecoveryChunkContainer;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class RecoveredFileChunksFileReaderTest {

	private RecoveredFileChunksFileReader recoveredFileChunksFileReader;

	private FilesIOService filesIOService = mock(FilesIOService.class);

	@BeforeEach
	void init() throws FileSystemException {
		reset(filesIOService);
		recoveredFileChunksFileReader = new RecoveredFileChunksFileReader(filesIOService);
	}

	@Test
	void shouldReadBackFromFile() throws IOException {
		when(filesIOService.getInputStream(any())).thenCallRealMethod();
		when(filesIOService.unPack(any())).thenCallRealMethod();

		RecoveryChunkContainer result = recoveredFileChunksFileReader.apply(RecoveryTestUtils.PATH);

		assertEquals(RecoveryTestUtils.TASK_ID, result.getTaskId());
	}

	@Test
	void shouldReturnEmptyContainerOnError() throws IOException {
		when(filesIOService.getInputStream(any())).thenThrow(new FileNotFoundException("success"));

		RecoveryChunkContainer result = recoveredFileChunksFileReader.apply(RecoveryTestUtils.PATH);

		assertEquals(RecoveryChunkContainer.EMPTY, result);
	}

}
