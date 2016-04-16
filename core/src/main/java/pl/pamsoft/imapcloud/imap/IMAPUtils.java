package pl.pamsoft.imapcloud.imap;

import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

class IMAPUtils {
	static final String IMAP_CLOUD_FOLDER_NAME = "IC";
	static final boolean NO_EXPUNGE = false;
	private static final int BEGIN_INDEX = 0;
	private static final int END_INDEX = 2;

	static String createFolderName(UploadChunkContainer ucc) {
		return generateFolderName(ucc.getFileHash());
	}

	static String createFolderName(FileChunk fileChunk) {
		return generateFolderName(fileChunk.getOwnerFile().getFileHash());
	}

	private static String generateFolderName(String hash) {
		return hash.substring(BEGIN_INDEX, END_INDEX);
	}
}
