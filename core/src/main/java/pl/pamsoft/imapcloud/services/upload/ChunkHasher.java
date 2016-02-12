package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.function.Function;

public class ChunkHasher implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkHasher.class);

	private MessageDigest md;

	public ChunkHasher(MessageDigest messageDigest) {
		this.md = messageDigest;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		md.update(chunk.getData());
		byte[] digest = md.digest();
		String sha256 = String.format("%x", new BigInteger(1, digest));
		LOG.debug("Hash generated in {}", stopwatch.stop());
		return new UploadChunkContainer(chunk.getFileDto(), chunk.getData(), chunk.getChunkNumber(), sha256);
	}
}
