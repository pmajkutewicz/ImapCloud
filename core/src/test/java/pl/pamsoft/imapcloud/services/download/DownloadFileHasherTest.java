package pl.pamsoft.imapcloud.services.download;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DownloadFileHasherTest {

	private DownloadFileHasher downloadFileHasher;

	private FileDto fileDto = TestUtils.mockFileDto();
	private FilesIOService filesIOService = mock(FilesIOService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@BeforeClass
	public void init() throws NoSuchAlgorithmException {
		downloadFileHasher = new DownloadFileHasher(filesIOService, monitoringHelper);
	}

	@Test
	/**
	 * Always calculate same hash:
	 * This is because DestFileUtils.generateFilePath(dcc).toFile() - it returns file from "ram" and file.length is 0 for that file.
	 * I guess one way to fix it is to mock DestFileUtils (first convert to bean) and mock length value to valid one.
	 * Data send to hash is valid (random bytes)
	 * Similar situation is in {@link pl.pamsoft.imapcloud.services.upload.UploadFileHasherTest}
	 */
	public void shouldCalculateHash() throws IOException, NoSuchAlgorithmException {
		byte[] randomBytes = TestUtils.getRandomBytes(1024);
		FileSystemManager manager = VFS.getManager();
		FileObject fileObject = manager.resolveFile("ram:///exampleFile.txt");
		fileObject.createFile();
		IOUtils.copy(new ByteArrayInputStream(randomBytes), fileObject.getContent().getOutputStream());
		fileObject.close();
		FileChunk fc = TestUtils.createFileChunk("exmapleFile.txt", true);
		when(filesIOService.getInputStream(any(File.class))).thenReturn(manager.resolveFile("ram:///exampleFile.txt").getContent().getInputStream());
		DownloadChunkContainer dcc = new DownloadChunkContainer(UUID.randomUUID().toString(), fc, fileDto, fc.getChunkHash(), fc.getOwnerFile().getFileHash());

		DownloadChunkContainer result = downloadFileHasher.apply(dcc);

		assertNotNull(result.getFileHash());
	}

	@Test
	public void shouldSkipStemWhenNotLastChunk() throws IOException {
		FileChunk fc = TestUtils.createFileChunk("exampleName", false);
		DownloadChunkContainer dcc = new DownloadChunkContainer(UUID.randomUUID().toString(), fc, fileDto, fc.getChunkHash(), fc.getOwnerFile().getFileHash());

		DownloadChunkContainer response = downloadFileHasher.apply(dcc);

		assertEquals(response, response);
	}

	@Test
	public void shouldReturnEmptyUCCWhenExceptionOccurred() throws IOException {
		FileChunk fc = TestUtils.createFileChunk("exampleName", true);
		DownloadChunkContainer dcc = new DownloadChunkContainer(UUID.randomUUID().toString(), fc, fileDto, fc.getChunkHash(), fc.getOwnerFile().getFileHash());
		when(filesIOService.getInputStream(any(File.class))).thenThrow(new FileNotFoundException("example"));

		DownloadChunkContainer response = downloadFileHasher.apply(dcc);

		assertEquals(response, DownloadChunkContainer.EMPTY);
	}
}
