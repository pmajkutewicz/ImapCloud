package pl.pamsoft.imapcloud.services.upload;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChunkEncrypterTest {

	private ChunkEncrypter chunkEncrypter;

	private CryptoService cryptoService = mock(CryptoService.class);
	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);
	private FileDto fileDto = TestUtils.mockFileDto();

	@BeforeAll
	void init() {
		PaddedBufferedBlockCipher cipher = mock(PaddedBufferedBlockCipher.class);
		when(cryptoService.getEncryptingCipher(any())).thenReturn(cipher);
		chunkEncrypter = new ChunkEncrypter(cryptoService, "exampleKey", monitoringHelper);
	}

	@Test
	void shouldEncryptChunk() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		byte[] out = TestUtils.getRandomBytes(1024);
		UploadChunkContainer ucc = createExampleUCC(in);

		when(cryptoService.encrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenReturn(out);

		UploadChunkContainer response = chunkEncrypter.apply(ucc);

		assertArrayEquals(out, response.getData());
	}

	@Test
	void shouldReturnEmptyUCCWhenExceptionOccurred() throws IOException, InvalidCipherTextException {
		byte[] in = TestUtils.getRandomBytes(1024);
		UploadChunkContainer ucc = createExampleUCC(in);
		when(cryptoService.encrypt(any(PaddedBufferedBlockCipher.class), eq(in))).thenThrow(new IOException("example"));

		UploadChunkContainer response = chunkEncrypter.apply(ucc);

		assertEquals(UploadChunkContainer.EMPTY, response);
	}

	private UploadChunkContainer createExampleUCC(byte[] in) {
		UploadChunkContainer ucc = new UploadChunkContainer(UUID.randomUUID().toString(), fileDto);
		ucc = UploadChunkContainer.addChunk(ucc, in.length, in.length, in, 1, false);
		return ucc;
	}

}
