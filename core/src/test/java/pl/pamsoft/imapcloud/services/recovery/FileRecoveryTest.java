package pl.pamsoft.imapcloud.services.recovery;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.IOException;
import java.nio.file.Paths;

import static java.util.Collections.singleton;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class FileRecoveryTest {

	private CryptoService cryptoService = new CryptoService();
	private FilesIOService filesIOService = mock(FilesIOService.class);
	private FileRepository fileRepository = mock(FileRepository.class);
	private FileChunkRepository fileChunkRepository = mock(FileChunkRepository.class);
	private AccountRepository accountRepository = mock(AccountRepository.class);

	private RecoveryChunkContainer exampleData;

	@BeforeMethod
	public void init() throws IOException {
		reset(fileRepository);
		reset(fileChunkRepository);
		reset(filesIOService);
		reset(accountRepository);
		when(filesIOService.getInputStream(any())).thenCallRealMethod();
		when(filesIOService.unPack(any())).thenCallRealMethod();
		RecoveredFileChunksFileReader reader = new RecoveredFileChunksFileReader(filesIOService);
		exampleData = reader.apply(Paths.get("src", "test", "resources", "b6a87830-1e0d-486c-a2c8-97efe71f01a5.ic.zip"));
	}

	@Test
	public void shouldRecoverSelectedFile() throws IOException {
		ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
		ArgumentCaptor<FileChunk> fileChunkCaptor = ArgumentCaptor.forClass(FileChunk.class);
		when(fileRepository.save(fileCaptor.capture())).thenAnswer(i -> fileCaptor.getValue());
		when(fileChunkRepository.save(fileChunkCaptor.capture())).thenAnswer(i -> fileChunkCaptor.getValue());
		when(accountRepository.getById(any())).thenReturn(exampleData.getAccount());
		String fileKey = "9c2e15e4-6b01-408c-a039-5681df67be84";

		new FileRecovery(singleton(fileKey), fileRepository, fileChunkRepository, accountRepository, cryptoService)
				.apply(exampleData);

		verify(fileChunkRepository, times(exampleData.getFileChunkMap().get(fileKey).size())).save(any());
		verify(fileRepository, times(1)).save(any());
		fileChunkCaptor.getAllValues().forEach(fc -> {
			assertEquals(fc.getOwnerFile().getFileUniqueId(), fileKey);
		});
	}

}
