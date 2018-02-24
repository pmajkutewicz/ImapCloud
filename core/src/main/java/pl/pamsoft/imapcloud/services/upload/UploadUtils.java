package pl.pamsoft.imapcloud.services.upload;

public class UploadUtils {

	//CSOFF: MagicNumber
	public static int toBytes(int maxChunkSizeMB) {
		return maxChunkSizeMB * 1024 * 1024;
	}
	//CSON: MagicNumber
}
