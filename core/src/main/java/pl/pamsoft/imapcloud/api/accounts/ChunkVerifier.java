package pl.pamsoft.imapcloud.api.accounts;

import pl.pamsoft.imapcloud.services.containers.VerifyChunkContainer;

import java.io.IOException;

/**
 * Implementing class should consider usage of pools and retrying methods.
 */
public interface ChunkVerifier {

	/**
	 * Verifies chunks in given target (imap, ftp, etc.)
	 *
	 * @param verifyChunkContainer Container with chunk data to verify.
	 * @return True if chunk exist in underlying storage. Otherwise false.
	 * @throws IOException In case of errors.
	 */
	boolean verify(VerifyChunkContainer verifyChunkContainer) throws IOException;

}
