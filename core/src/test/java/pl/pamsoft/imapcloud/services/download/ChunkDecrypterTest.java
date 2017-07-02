package pl.pamsoft.imapcloud.services.download;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class ChunkDecrypterTest {

	private ChunkDecrypter chunkDecrypter;

	private CryptoService cryptoService = mock(CryptoService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);
	private FileDto fileDto = TestUtils.mockFileDto();

	@BeforeClass
	public void init() {
		PaddedBufferedBlockCipher cipher = mock(PaddedBufferedBlockCipher.class);
		when(cryptoService.getDecryptingCipher(any())).thenReturn(cipher);
		chunkDecrypter = new ChunkDecrypter(cryptoService, "exampleKey", monitoringHelper);
	}

	@Test
	public void shouldEncryptChunk() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		byte[] out = TestUtils.getRandomBytes(1024);
		DownloadChunkContainer ucc = createExampleDCC(in);
		when(cryptoService.decrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenReturn(out);

		DownloadChunkContainer response = chunkDecrypter.apply(ucc);

		assertEquals(response.getData(), out);
	}

	@Test
	public void shouldReturnEmptyUCCWhenExceptionOccurred() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		DownloadChunkContainer ucc = createExampleDCC(in);
		when(cryptoService.decrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenThrow(new IOException("example"));

		DownloadChunkContainer response = chunkDecrypter.apply(ucc);

		assertEquals(response, DownloadChunkContainer.EMPTY);
	}

	private DownloadChunkContainer createExampleDCC(byte[] in) {
		FileChunk fc= TestUtils.createFileChunk("example", false);
		DownloadChunkContainer dcc = new DownloadChunkContainer(UUID.randomUUID().toString(), fc, fileDto, fc.getChunkHash(), fc.getOwnerFile().getFileHash());
		dcc = DownloadChunkContainer.addData(dcc, in);
		return dcc;
	}

}
