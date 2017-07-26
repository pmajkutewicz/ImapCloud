package pl.pamsoft.imapcloud.storage.vfs;

public class VfsUtils {
	private static final int BEGIN_INDEX = 0;
	private static final int END_INDEX = 2;

	protected static String createFolderName(String fileHash) {
		return generateFolderName(fileHash);
	}

	protected static String createFileName(String fileName, int partNumber) {
		return String.format("%s.%05d", fileName, partNumber);
	}

	private static String generateFolderName(String hash) {
		return hash.substring(BEGIN_INDEX, END_INDEX);
	}

}
