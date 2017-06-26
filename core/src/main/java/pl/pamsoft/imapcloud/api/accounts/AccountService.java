package pl.pamsoft.imapcloud.api.accounts;

import pl.pamsoft.imapcloud.entity.Account;

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
}

