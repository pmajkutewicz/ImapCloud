package pl.pamsoft.imapcloud.services.upload;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class UploadFileHasherTest {

	private UploadFileHasher uploadFileHasher;

	private FileDto fileDto = TestUtils.mockFileDto();
	private FilesIOService filesIOService = mock(FilesIOService.class);
	private PerformanceDataService performanceDataService = mock(PerformanceDataService.class);

	@BeforeClass
	public void setup() throws NoSuchAlgorithmException {
		uploadFileHasher = new UploadFileHasher(filesIOService, performanceDataService);
	}

	@Test
	public void shouldCalculateHash() throws IOException, NoSuchAlgorithmException {
		byte[] randomBytes = TestUtils.getRandomBytes(1024);
		FileSystemManager manager = VFS.getManager();
		FileObject fileObject = manager.resolveFile("ram:///exampleFile.txt");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IOUtils.copy(new ByteArrayInputStream(randomBytes), outputStream);
		fileObject.createFile();
		fileObject.getContent().write(outputStream);
		File file = new File(fileObject.getName().getPath());
		when(filesIOService.getFile(eq(fileDto))).thenReturn(file);
		when(filesIOService.getInputStream(file)).thenReturn(fileObject.getContent().getInputStream());
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);

		UploadChunkContainer result = uploadFileHasher.apply(ucc);

		assertNotNull(result.getFileHash());
	}

	@Test
	public void shouldReturnEmptyUCCWhenExceptionOccurred() throws IOException {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);
		when(filesIOService.getInputStream(any(File.class))).thenThrow(new FileNotFoundException("example"));

		UploadChunkContainer response = uploadFileHasher.apply(ucc);

		assertEquals(response, UploadChunkContainer.EMPTY);
	}
}
