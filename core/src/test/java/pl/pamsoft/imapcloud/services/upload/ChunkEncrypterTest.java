package pl.pamsoft.imapcloud.services.upload;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class ChunkEncrypterTest {

	private ChunkEncrypter chunkEncrypter;

	private CryptoService cryptoService = mock(CryptoService.class);
	private Statistics statistics = mock(Statistics.class);
	private PerformanceDataService performanceDataService = mock(PerformanceDataService.class);
	private FileDto fileDto= mock(FileDto.class);

	@BeforeClass
	public void setup() {
		when(fileDto.getName()).thenReturn("exampleName");
		when(fileDto.getAbsolutePath()).thenReturn("/path/exampleName.txt");
		chunkEncrypter = new ChunkEncrypter(cryptoService, "exampleKey", statistics, performanceDataService);
	}

	@Test
	public void shouldEncryptChunk() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		byte[] out = TestUtils.getRandomBytes(1024);
		UploadChunkContainer ucc = createExampleUCC(in);
		when(cryptoService.encrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenReturn(out);

		UploadChunkContainer response = chunkEncrypter.apply(ucc);

		assertEquals(response.getData(), out);
	}

	@Test
	public void shouldReturnEmptyUCCWhenExceptionOccurred() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		byte[] out = TestUtils.getRandomBytes(1024);
		UploadChunkContainer ucc = createExampleUCC(in);
		when(cryptoService.encrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenThrow(new IOException("example"));

		UploadChunkContainer response = chunkEncrypter.apply(ucc);

		assertEquals(response, UploadChunkContainer.EMPTY);
	}

	private UploadChunkContainer createExampleUCC(byte[] in) {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);
		ucc = UploadChunkContainer.addChunk(ucc, in.length, in.length, in, 1, false);
		return ucc;
	}

}
