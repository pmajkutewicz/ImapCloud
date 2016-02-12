package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.crypto.CryptoService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.function.Function;

public class ChunkEncoder implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkEncoder.class);
	private CryptoService cs;
	private byte[] publicKey;

	public ChunkEncoder(CryptoService cryptoService, String publicKey) {
		this.cs = cryptoService;
		this.publicKey = Base64Utils.decodeFromString(publicKey);
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer uploadChunkContainer) {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			byte[] encode = cs.encode(publicKey, uploadChunkContainer.getData());
			LOG.debug("Chunk of {} for file {} created in {}", uploadChunkContainer.getData().length, uploadChunkContainer.getFileDto().getAbsolutePath(), stopwatch.stop());
		} catch (InvalidKeyException | NoSuchPaddingException |
			NoSuchAlgorithmException | BadPaddingException |
			IllegalBlockSizeException | InvalidKeySpecException e) {
			//TODO: e.printStackTrace();
		}
		return null;
	}
}
