package pl.pamsoft.imapcloud.services.recovery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.dto.RecoveredFileDto;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.containers.RecoveryChunkContainer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class RCCtoRecoveredFileDtoConverterTest {

	private RCCtoRecoveredFileDtoConverter rcCtoFileDtoConverter;

	private FilesIOService filesIOService = mock(FilesIOService.class);
	private CryptoService cryptoService = new CryptoService();
	private RecoveryChunkContainer exampleData;

	@BeforeEach
	void init() throws IOException {
		reset(filesIOService);
		when(filesIOService.getInputStream(any())).thenCallRealMethod();
		when(filesIOService.unPack(any())).thenCallRealMethod();
		RecoveredFileChunksFileReader recoveredFileChunksFileReader = new RecoveredFileChunksFileReader(filesIOService);
		rcCtoFileDtoConverter = new RCCtoRecoveredFileDtoConverter(cryptoService);
		exampleData = recoveredFileChunksFileReader.apply(
			Paths.get("src", "test", "resources", "b6a87830-1e0d-486c-a2c8-97efe71f01a5.ic.zip")
		);
	}

	@Test
	void shouldConvertAllRecoveredFilesToDto() throws IOException {
		List<RecoveredFileDto> results = rcCtoFileDtoConverter.apply(exampleData);

		exampleData.getFileMap()
			.values()
			.forEach(f -> assertTrue(fileSuccessfullyConverter(f.getFileUniqueId(), results)));
	}

	private boolean fileSuccessfullyConverter(String uniqueFileId, List<RecoveredFileDto> recoveredFiles) {
		return recoveredFiles.stream().anyMatch(f -> uniqueFileId.equals(f.getFileUniqueId()));
	}

}
