package pl.pamsoft.imapcloud.storage.imap;

import pl.pamsoft.imapcloud.api.containers.UploadChunkContainer;

class IMAPUtils {

	protected static final String IMAP_CLOUD_FOLDER_NAME = "IC";
	protected static final boolean NO_EXPUNGE = false;
	protected static final boolean EXPUNGE = true;
	private static final int BEGIN_INDEX = 0;
	private static final int END_INDEX = 2;

	protected static String createFolderName(UploadChunkContainer ucc) {
		return generateFolderName(ucc.getFileHash());
	}

	protected static String createFolderName(String fileHash) {
		return generateFolderName(fileHash);
	}

	private static String generateFolderName(String hash) {
		return hash.substring(BEGIN_INDEX, END_INDEX);
	}
}
