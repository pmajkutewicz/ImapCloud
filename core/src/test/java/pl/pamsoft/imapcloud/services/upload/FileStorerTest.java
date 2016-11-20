package pl.pamsoft.imapcloud.services.upload;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.nio.file.FileAlreadyExistsException;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class FileStorerTest {

	public static final String EXAMPLE_ID = "someId";
	public static final String EXAMPLE_UNIQUE_FILE_ID = "uniqueFileId";
	private FileStorer fileStorer;

	private FileServices fileServices = mock(FileServices.class);
	private Account account = mock(Account.class);
	@SuppressWarnings("unchecked")
	private Consumer<UploadChunkContainer> updateProgress = (Consumer<UploadChunkContainer>) mock(Consumer.class);
	@SuppressWarnings("unchecked")
	private Consumer<UploadChunkContainer> broadcastTaskProgress = (Consumer<UploadChunkContainer>) mock(Consumer.class);

	@BeforeClass
	public void init() {
		fileStorer = new FileStorer(fileServices, account, updateProgress, broadcastTaskProgress);
	}

	@Test
	public void shouldStoreFile() throws FileAlreadyExistsException {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), TestUtils.mockFileDto());
		when(fileServices.saveFile(eq(ucc), eq(account))).thenReturn(mockFile());

		UploadChunkContainer result = fileStorer.apply(ucc);

		verify(fileServices, times(1)).saveFile(eq(ucc), eq(account));
		assertEquals(result.getFileUniqueId(), EXAMPLE_UNIQUE_FILE_ID);
		assertEquals(result.getSavedFileId(), EXAMPLE_ID);
	}

	@Test
	public void shouldReturnEmptyUCCWhenExceptionOccurred() throws FileAlreadyExistsException {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), TestUtils.mockFileDto());
		when(fileServices.saveFile(eq(ucc), eq(account))).thenThrow(new FileAlreadyExistsException("exampleFAEE"));

		UploadChunkContainer result = fileStorer.apply(ucc);

		assertEquals(result, UploadChunkContainer.EMPTY);
	}

	private File mockFile() {
		File file = new File();
		file.setId(EXAMPLE_ID);
		file.setFileUniqueId(EXAMPLE_UNIQUE_FILE_ID);
		return file;
	}

}
