package pl.pamsoft.imapcloud.services.download;

import org.mockito.Mockito;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FileHashVerifierTest {

	@Test
	public void shouldNotVerifyHashIfNotLastChunk() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", false);
		FileChunk spy = Mockito.spy(fc);
		DownloadChunkContainer dcc = new DownloadChunkContainer("id", fc, mock(FileDto.class));

		DownloadChunkContainer result = new FileHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(result, dcc);
		assertTrue(invalidFileIds.isEmpty());
		verify(spy, times(0)).getOwnerFile();
	}


	@Test
	public void shouldVerifyHashIfLastChunk() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", true);
		DownloadChunkContainer dcc = DownloadChunkContainer.addFileHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class)), TestUtils.EXAMPLE_FILE_HASH);

		DownloadChunkContainer result = new FileHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(result, dcc);
		assertTrue(invalidFileIds.isEmpty());
	}

	@Test
	public void shouldAddFileToInvalidFileIdsWhenMismatch() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", true);
		DownloadChunkContainer dcc = DownloadChunkContainer.addFileHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class)), "invalidHash");

		DownloadChunkContainer result = new FileHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(result, DownloadChunkContainer.EMPTY);
		assertFalse(invalidFileIds.isEmpty());
		assertEquals(1,  invalidFileIds.size());
		assertEquals(invalidFileIds.get(0), TestUtils.FILE_ID);
	}
}
