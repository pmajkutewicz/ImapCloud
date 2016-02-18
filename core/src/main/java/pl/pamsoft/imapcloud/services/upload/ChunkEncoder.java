package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.io.IOException;
import java.util.function.Function;

public class ChunkEncoder implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkEncoder.class);
	private CryptoService cs;
	private PaddedBufferedBlockCipher encryptingCipher;

	public ChunkEncoder(CryptoService cryptoService, String key) {
		this.cs = cryptoService;
		byte[] keyBytes = ByteUtils.fromHexString(key);
		encryptingCipher = cs.getEncryptingCipher(keyBytes);
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer uploadChunkContainer) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			byte[] encrypted = cs.encrypt(encryptingCipher, uploadChunkContainer.getData());
			LOG.debug("{} chunk encrypted in {} (size: {} -> {}",
				uploadChunkContainer.getFileDto().getAbsolutePath(), stopwatch.stop(), uploadChunkContainer.getData().length, encrypted.length);
			return UploadChunkContainer.addEncryptedData(uploadChunkContainer, encrypted);
		} catch (InvalidCipherTextException | IOException e) {
			LOG.error("Error encrypting chunk", e);
		}
		LOG.warn("Returning EMPTY from ChunkEncoder");
		return UploadChunkContainer.EMPTY;
	}
}
