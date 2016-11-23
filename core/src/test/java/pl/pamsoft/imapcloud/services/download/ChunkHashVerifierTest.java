package pl.pamsoft.imapcloud.services.download;

import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ChunkHashVerifierTest {

	private static final String EXPECTED_HASH = "expectedHash";
	private static final String FILE_ID = "fileId";

	@Test
	public void shouldVerifyHashIfLastChunk() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = createFileChunk();
		DownloadChunkContainer dcc = DownloadChunkContainer.addChunkHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class)), EXPECTED_HASH);

		DownloadChunkContainer result = new ChunkHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(result, dcc);
		assertTrue(invalidFileIds.isEmpty());
	}

	@Test
	public void shouldAddFileToInvalidFileIdsWhenMismatch() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = createFileChunk();
		DownloadChunkContainer dcc = DownloadChunkContainer.addFileHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class)), "invalidHash");

		DownloadChunkContainer result = new ChunkHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(result, DownloadChunkContainer.EMPTY);
		assertFalse(invalidFileIds.isEmpty());
		assertEquals(1,  invalidFileIds.size());
		assertEquals(invalidFileIds.get(0), FILE_ID);
	}

	private FileChunk createFileChunk() {
		File f = createFile();
		FileChunk fc = new FileChunk();
		fc.setLastChunk(true);
		fc.setOwnerFile(f);
		fc.setChunkHash(EXPECTED_HASH);
		return fc;
	}

	private File createFile() {
		File f = new File();
		f.setFileUniqueId(FILE_ID);
		f.setName("fileName");
		return f;
	}
}
