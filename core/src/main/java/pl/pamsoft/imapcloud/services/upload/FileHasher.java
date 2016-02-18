package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.function.Function;

public class FileHasher implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileHasher.class);
	private static final int MEGABYTE = 1024 * 1024;

	private MessageDigest md;

	public FileHasher(MessageDigest messageDigest) {
		this.md = messageDigest;
	}

	@Override
	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		try (FileInputStream inputStream = new FileInputStream(new File(chunk.getFileDto().getAbsolutePath()))) {
			byte[] bytesBuffer = new byte[MEGABYTE];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
				md.update(bytesBuffer, 0, bytesRead);
			}
			byte[] hashedBytes = md.digest();
			String hash = ByteUtils.toHexString(hashedBytes);
			LOG.debug("File hash generated in {}", stopwatch.stop());
			return UploadChunkContainer.addFileHash(chunk, hash);
		} catch (IOException ex) {
			LOG.error(String.format("Can't calculate hash for file: %s", chunk.getFileDto().getAbsolutePath()), ex);
		}
		LOG.warn("Returning EMPTY from FileHasher");
		return UploadChunkContainer.EMPTY;
	}
}
