package pl.pamsoft.imapcloud.imap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.services.CryptoService;

import java.io.File;

class IMAPUtils {
	static final String IMAP_CLOUD_FOLDER_NAME = "IC";
	static final boolean NO_EXPUNGE = false;

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	static String createFolderName(CryptoService cs, String absoluteFilePath) {
		String absolutePath = new File(absoluteFilePath).getParent();
		return cs.rot13(absolutePath.replace(File.separator, ""));
	}

}
