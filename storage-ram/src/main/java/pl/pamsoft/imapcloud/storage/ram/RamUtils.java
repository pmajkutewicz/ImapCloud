package pl.pamsoft.imapcloud.storage.ram;

public class RamUtils {
	private static final int BEGIN_INDEX = 0;
	private static final int END_INDEX = 2;

	protected static String createFolderName(String fileHash) {
		return generateFolderName(fileHash);
	}

	private static String generateFolderName(String hash) {
		return hash.substring(BEGIN_INDEX, END_INDEX);
	}
}
