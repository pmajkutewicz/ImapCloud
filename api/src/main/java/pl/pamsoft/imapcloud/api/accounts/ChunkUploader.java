package pl.pamsoft.imapcloud.api.accounts;

import pl.pamsoft.imapcloud.api.containers.UploadChunkContainer;

import java.io.IOException;
import java.util.Map;

/**
 * Implementing class should consider usage of pools and retrying methods.
 */
public interface ChunkUploader {

	/**
	 * Uploads file chunk to given target (imap, ftp, etc.)
	 *
	 * @param uploadChunkContainer Container with chunk data to store.
	 * @param metadata             Metadata that should be also stored. Used in recovery process.
	 * @return Unique ID of uploaded chunk. ChunkDownload must be able to use in chunk downloading process.
	 * @throws IOException In case of upload errors.
	 */
	String upload(UploadChunkContainer uploadChunkContainer, Map<String, String> metadata) throws IOException;

}
