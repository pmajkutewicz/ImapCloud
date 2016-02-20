package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FileHasher implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileHasher.class);
	private static final int MEGABYTE = 1024 * 1024;

	private MessageDigest md;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public FileHasher(MessageDigest messageDigest, Statistics statistics, PerformanceDataService performanceDataService) {
		this.md = messageDigest;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
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
			statistics.add(StatisticType.FILE_HASH, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.FILE_HASH, stopwatch.elapsed(TimeUnit.MICROSECONDS)));
			LOG.debug("File hash generated in {}", stopwatch);
			return UploadChunkContainer.addFileHash(chunk, hash);
		} catch (IOException ex) {
			LOG.error(String.format("Can't calculate hash for file: %s", chunk.getFileDto().getAbsolutePath()), ex);
		}
		LOG.warn("Returning EMPTY from FileHasher");
		return UploadChunkContainer.EMPTY;
	}
}
