package pl.pamsoft.imapcloud.services.upload;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.nio.file.FileAlreadyExistsException;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileStorerTest {

	private static final Long EXAMPLE_ID = 123L;
	private static final String EXAMPLE_UNIQUE_FILE_ID = "uniqueFileId";
	private FileStorer fileStorer;

	private FileServices fileServices = mock(FileServices.class);
	private Account account = mock(Account.class);
	@SuppressWarnings("unchecked")
	private Consumer<UploadChunkContainer> updateProgress = (Consumer<UploadChunkContainer>) mock(Consumer.class);

	@BeforeAll
	void init() {
		fileStorer = new FileStorer(fileServices, account, updateProgress);
	}

	@Test
	void shouldStoreFile() throws FileAlreadyExistsException {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), TestUtils.mockFileDto());
		when(fileServices.saveFile(eq(ucc), eq(account))).thenReturn(mockFile());

		UploadChunkContainer result = fileStorer.apply(ucc);

		verify(fileServices, times(1)).saveFile(eq(ucc), eq(account));
		assertEquals(EXAMPLE_UNIQUE_FILE_ID, result.getFileUniqueId());
		assertEquals(EXAMPLE_ID, result.getSavedFileId());
	}

	@Test
	void shouldReturnEmptyUCCWhenExceptionOccurred() throws FileAlreadyExistsException {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), TestUtils.mockFileDto());
		when(fileServices.saveFile(eq(ucc), eq(account))).thenThrow(new FileAlreadyExistsException("exampleFAEE"));

		UploadChunkContainer result = fileStorer.apply(ucc);

		assertEquals(UploadChunkContainer.EMPTY, result);
	}

	private File mockFile() {
		File file = new File();
		file.setId(EXAMPLE_ID);
		file.setFileUniqueId(EXAMPLE_UNIQUE_FILE_ID);
		return file;
	}

}
