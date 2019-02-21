package pl.pamsoft.imapcloud.services.upload;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.download.DownloadFileHasherTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UploadFileHasherTest {

	private UploadFileHasher uploadFileHasher;

	private FileDto fileDto = TestUtils.mockFileDto();
	private FilesIOService filesIOService = mock(FilesIOService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@BeforeAll
	void init() throws NoSuchAlgorithmException {
		uploadFileHasher = new UploadFileHasher(filesIOService, monitoringHelper);
	}

	@AfterEach
	void clean() {
		Mockito.reset(filesIOService, monitoringHelper);
	}

	@Test
	/**
	 * @see DownloadFileHasherTest#shouldCalculateHash()
	 */
	void shouldCalculateHash() throws IOException, NoSuchAlgorithmException {
		FileObject fileObject = getTempFile();
		File file = new File(fileObject.getName().getPath());
		when(filesIOService.getFile(eq(fileDto))).thenReturn(file);
		when(filesIOService.getInputStream(file)).thenReturn(fileObject.getContent().getInputStream());
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);

		UploadChunkContainer result = uploadFileHasher.apply(ucc);

		assertNotNull(result.getFileHash());
	}

	@Test
	void shouldReturnEmptyUCCWhenExceptionOccurred() throws IOException {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);
		FileObject fileObject = getTempFile();
		File file = new File(fileObject.getName().getPath());
		when(filesIOService.getFile(fileDto)).thenReturn(file);
		when(filesIOService.getInputStream(any(File.class))).thenThrow(new FileNotFoundException("example"));

		UploadChunkContainer response = uploadFileHasher.apply(ucc);

		assertEquals(UploadChunkContainer.EMPTY, response);
	}

	private FileObject getTempFile() throws IOException {
		byte[] randomBytes = TestUtils.getRandomBytes(1024);
		FileSystemManager manager = VFS.getManager();
		FileObject fileObject = manager.resolveFile("ram:///exampleFile.txt");
		fileObject.createFile();
		IOUtils.copy(new ByteArrayInputStream(randomBytes), fileObject.getContent().getOutputStream());
		fileObject.close();
		return fileObject;
	}
}
