package pl.pamsoft.imapcloud.services.common;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.security.MessageDigest;

public interface ChunkHasher {

	default String hash(byte[] data) {
		byte[] digest = getMessageDigest().digest(data);
		return ByteUtils.toHexString(digest);
	}

	MessageDigest getMessageDigest();
}
