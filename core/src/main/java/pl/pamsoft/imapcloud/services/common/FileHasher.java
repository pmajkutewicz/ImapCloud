package pl.pamsoft.imapcloud.services.common;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import pl.pamsoft.imapcloud.services.FilesIOService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public interface FileHasher {
	int MEGABYTE = 1024 * 1024;

	default String hash(File file) throws IOException {
		try (InputStream inputStream = getFilesIOService().getFileInputStream(file)) {
			byte[] bytesBuffer = new byte[MEGABYTE];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				getMessageDigest().update(bytesBuffer, 0, bytesRead);
			}
			byte[] hashedBytes = getMessageDigest().digest();
			return ByteUtils.toHexString(hashedBytes);
		}
	}

	MessageDigest getMessageDigest();

	FilesIOService getFilesIOService();
}
