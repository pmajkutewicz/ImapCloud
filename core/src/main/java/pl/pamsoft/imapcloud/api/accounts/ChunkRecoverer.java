package pl.pamsoft.imapcloud.api.accounts;

import pl.pamsoft.imapcloud.services.containers.RecoveryChunkContainer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Implementing class should consider usage of pools and retrying methods.
 */
public interface ChunkRecoverer {

	/**
	 * Recovers chunks in given target (imap, ftp, etc.)
	 *
	 * @param recoveryChunkContainer Container with chunk data to recover.
	 * @return True if was recovered in underlying storage. Otherwise false.
	 * @throws IOException In case of errors.
	 */
	List<Map<String, String>> recover(RecoveryChunkContainer recoveryChunkContainer) throws IOException;

}
