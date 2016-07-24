package pl.pamsoft.imapcloud.services.recovery;

import org.apache.commons.vfs2.FileSystemException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class RecoveredFileChunksFileReaderTest {

	private RecoveredFileChunksFileReader recoveredFileChunksFileReader;

	private FilesIOService filesIOService = mock(FilesIOService.class);

	private final String taskId = "b6a87830-1e0d-486c-a2c8-97efe71f01a5";
	private final Path path = Paths.get("src", "test", "resources", taskId + ".ic.zip");

	@BeforeMethod
	public void setup() throws FileSystemException {
		reset(filesIOService);
		recoveredFileChunksFileReader = new RecoveredFileChunksFileReader(filesIOService);
	}

	@Test
	public void shouldReadBackFromFile() throws IOException {
		when(filesIOService.getInputStream(any())).thenCallRealMethod();
		when(filesIOService.unPack(any())).thenCallRealMethod();

		RecoveryChunkContainer result = recoveredFileChunksFileReader.apply(path);

		Assert.assertEquals(result.getTaskId(), taskId);
	}

	@Test
	public void shouldReturnEmptyContainerOnError() throws IOException {
		when(filesIOService.getInputStream(any())).thenThrow(new FileNotFoundException("success"));

		RecoveryChunkContainer result = recoveredFileChunksFileReader.apply(path);

		Assert.assertEquals(result, RecoveryChunkContainer.EMPTY);
	}

}
