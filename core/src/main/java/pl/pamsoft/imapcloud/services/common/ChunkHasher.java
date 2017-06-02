package pl.pamsoft.imapcloud.services.common;

import net.openhft.hashing.LongHashFunction;

public interface ChunkHasher {

	default String hash(byte[] data) {
		return Long.toHexString(LongHashFunction.xx().hashBytes(data));
	}
}
