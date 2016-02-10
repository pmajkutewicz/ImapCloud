package pl.pamsoft.imapcloud.services.upload;

import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.function.Function;

public class ChunkHasher implements Function<UploadChunkContainer, UploadChunkContainer> {

	private MessageDigest md;

	public ChunkHasher(MessageDigest messageDigest) {
		this.md = messageDigest;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		md.update(chunk.getData());
		byte[] digest = md.digest();
		String sha256 = String.format("%x", new BigInteger(1, digest));
		return new UploadChunkContainer(chunk.getFileDto(), chunk.getData(), chunk.getChunkNumber(), sha256);
	}
}
