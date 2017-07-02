package pl.pamsoft.imapcloud.api.accounts;

public interface AccountService {

	/**
	 * Unique ID of destination type. Must be set as parameter in accounts.yml
	 */
	String getType();

	/**
	 * Returns Chunk uploader.
	 *
	 * @param account Account associated with uploader.
	 * @return Chunk uploader.
	 */
	ChunkUploader getChunkUploader(Account account);

	/**
	 * Returns Chunk downloader.
	 *
	 * @param account Account associated with downloader.
	 * @return Chunk downloader.
	 */
	ChunkDownloader getChunkDownloader(Account account);

	/**
	 * Returns Chunk deleter.
	 *
	 * @param account Account associated with deleter.
	 * @return Chunk deleter.
	 */
	ChunkDeleter getChunkDeleter(Account account);

	/**
	 * Returns Chunk verifier.
	 *
	 * @param account Account associated with verifier.
	 * @return Chunk verifier.
	 */
	ChunkVerifier getChunkVerifier(Account account);

	/**
	 * Returns Chunk recoverer.
	 *
	 * @param account Account associated with recoverer.
	 * @return Chunk recoverer.
	 */
	ChunkRecoverer getChunkRecoverer(Account account);
}

