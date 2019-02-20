package pl.pamsoft.imapcloud.services.download;

import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ChunkHashVerifierTest {

	private static final String EXPECTED_HASH = "expectedHash";
	private static final String FILE_ID = "fileId";

	@Test
	void shouldVerifyHashIfLastChunk() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = createFileChunk();
		DownloadChunkContainer dcc = DownloadChunkContainer.addChunkHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class), fc.getChunkHash(), fc.getOwnerFile().getFileHash()), EXPECTED_HASH);

		DownloadChunkContainer result = new ChunkHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(dcc, result);
		assertTrue(invalidFileIds.isEmpty());
	}

	@Test
	void shouldAddFileToInvalidFileIdsWhenMismatch() {
		List<String> invalidFileIds = new CopyOnWriteArrayList<>();
		FileChunk fc = createFileChunk();
		DownloadChunkContainer dcc = DownloadChunkContainer.addFileHash(
			new DownloadChunkContainer("id", fc, mock(FileDto.class), fc.getChunkHash(), fc.getOwnerFile().getFileHash()), "invalidHash");

		DownloadChunkContainer result = new ChunkHashVerifier(invalidFileIds).apply(dcc);

		assertEquals(DownloadChunkContainer.EMPTY, result);
		assertFalse(invalidFileIds.isEmpty());
		assertEquals(invalidFileIds.size(), 1);
		assertEquals(FILE_ID, invalidFileIds.get(0));
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
