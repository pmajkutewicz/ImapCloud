package pl.pamsoft.imapcloud.api.accounts;

import pl.pamsoft.imapcloud.api.containers.DeleteChunkContainer;

import java.io.IOException;

/**
 * Implementing class should consider usage of pools and retrying methods.
 */
public interface ChunkDeleter {

	/**
	 * Deletes file/chunk from given target (imap, ftp, etc.)
	 *
	 * @param deleteChunkContainer Container with chunk data to delete.
	 * @return True if chunk/file was deleted. Otherwise false.
	 * @throws IOException In case of errors.
	 */
	boolean delete(DeleteChunkContainer deleteChunkContainer) throws IOException;

}
