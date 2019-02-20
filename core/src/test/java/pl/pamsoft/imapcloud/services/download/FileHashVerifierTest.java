package pl.pamsoft.imapcloud.services.download;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FileHashVerifierTest {

	@Test
	void shouldNotVerifyHashIfNotLastChunk() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", false);
		FileChunk spy = Mockito.spy(fc);
		DownloadChunkContainer dcc = new DownloadChunkContainer("id", fc, mock(FileDto.class), fc.getChunkHash(), fc.getOwnerFile().getFileHash());

		DownloadChunkContainer result = new FileHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(dcc, result);
		assertTrue(invalidFileIds.isEmpty());
		verify(spy, times(0)).getOwnerFile();
	}


	@Test
	void shouldVerifyHashIfLastChunk() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", true);
		DownloadChunkContainer dcc = DownloadChunkContainer.addFileHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class), fc.getChunkHash(), fc.getOwnerFile().getFileHash()), TestUtils.EXAMPLE_FILE_HASH);

		DownloadChunkContainer result = new FileHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(dcc, result);
		assertTrue(invalidFileIds.isEmpty());
	}

	@Test
	void shouldAddFileToInvalidFileIdsWhenMismatch() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", true);
		DownloadChunkContainer dcc = DownloadChunkContainer.addFileHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class), fc.getChunkHash(), fc.getOwnerFile().getFileHash()), "invalidHash");

		DownloadChunkContainer result = new FileHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(DownloadChunkContainer.EMPTY, result);
		assertFalse(invalidFileIds.isEmpty());
		assertEquals(invalidFileIds.size(), 1);
		assertEquals(TestUtils.FILE_ID, invalidFileIds.get(0));
	}
}
