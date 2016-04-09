package pl.pamsoft.imapcloud.imap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.io.File;

class IMAPUtils {
	static final String IMAP_CLOUD_FOLDER_NAME = "IC";
	static final boolean NO_EXPUNGE = false;
	private static final int BEGIN_INDEX = 0;
	private static final int END_INDEX = 2;

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	static String createFolderName(CryptoService cs, String absoluteFilePath) {
		String absolutePath = new File(absoluteFilePath).getParent();
		return cs.rot13(absolutePath.replace(File.separator, ""));
	}

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
