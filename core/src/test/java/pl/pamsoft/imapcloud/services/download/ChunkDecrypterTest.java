package pl.pamsoft.imapcloud.services.download;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChunkDecrypterTest {

	private ChunkDecrypter chunkDecrypter;

	private CryptoService cryptoService = mock(CryptoService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);
	private FileDto fileDto = TestUtils.mockFileDto();

	@BeforeAll
	void init() {
		PaddedBufferedBlockCipher cipher = mock(PaddedBufferedBlockCipher.class);
		when(cryptoService.getDecryptingCipher(any())).thenReturn(cipher);
		chunkDecrypter = new ChunkDecrypter(cryptoService, "exampleKey", monitoringHelper);
	}

	@Test
	void shouldEncryptChunk() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		byte[] out = TestUtils.getRandomBytes(1024);
		DownloadChunkContainer ucc = createExampleDCC(in);
		when(cryptoService.decrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenReturn(out);

		DownloadChunkContainer response = chunkDecrypter.apply(ucc);

		assertArrayEquals(out, response.getData());
	}

	@Test
	void shouldReturnEmptyUCCWhenExceptionOccurred() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		DownloadChunkContainer ucc = createExampleDCC(in);
		when(cryptoService.decrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenThrow(new IOException("example"));

		DownloadChunkContainer response = chunkDecrypter.apply(ucc);

		assertEquals(DownloadChunkContainer.EMPTY, response);
	}

	private DownloadChunkContainer createExampleDCC(byte[] in) {
		FileChunk fc= TestUtils.createFileChunk("example", false);
		DownloadChunkContainer dcc = new DownloadChunkContainer(UUID.randomUUID().toString(), fc, fileDto, fc.getChunkHash(), fc.getOwnerFile().getFileHash());
		dcc = DownloadChunkContainer.addData(dcc, in);
		return dcc;
	}

}
