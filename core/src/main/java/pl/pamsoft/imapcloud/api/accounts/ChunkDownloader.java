package pl.pamsoft.imapcloud.api.accounts;

import pl.pamsoft.imapcloud.api.containers.DownloadChunkContainer;

import java.io.IOException;

/**
 * Implementing class should consider usage of pools and retrying methods.
 */
public interface ChunkDownloader {

	/**
	 * Downloads file chunk from given target (imap, ftp, etc.).
	 *
	 * @param downloadChunkContainer Container with chunk data to download.
	 * @throws IOException In case of download errors.
	 */
	byte[] download(DownloadChunkContainer downloadChunkContainer) throws IOException;

}
