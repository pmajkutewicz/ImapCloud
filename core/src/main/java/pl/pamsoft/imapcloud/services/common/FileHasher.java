package pl.pamsoft.imapcloud.services.common;

import net.openhft.hashing.LongHashFunction;
import pl.pamsoft.imapcloud.services.FilesIOService;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public interface FileHasher {

	default String hash(File file) throws IOException {
		try (DataInputStream inputStream = new DataInputStream(getFilesIOService().getInputStream(file))) {
			long hash = LongHashFunction.xx_r39().hash(inputStream, new DataInputStreamAccess(), 0, file.length());
			return Long.toHexString(hash);
		}
	}

	FilesIOService getFilesIOService();
}
