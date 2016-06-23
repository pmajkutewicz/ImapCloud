package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.common.FileHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.function.Function;

public class UploadFileHasher implements Function<UploadChunkContainer, UploadChunkContainer>, FileHasher {

	private static final Logger LOG = LoggerFactory.getLogger(UploadFileHasher.class);

	private MessageDigest md;
	private FilesIOService filesIOService;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public UploadFileHasher(MessageDigest messageDigest, FilesIOService filesIOService, Statistics statistics, PerformanceDataService performanceDataService) {
		this.md = messageDigest;
		this.filesIOService = filesIOService;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		LOG.debug("Hashing file {}", chunk.getFileDto().getName());
		Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			String hash = hash(filesIOService.getFile(chunk.getFileDto()));
			statistics.add(StatisticType.FILE_HASH, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.FILE_HASH, stopwatch));
			LOG.debug("File hash generated in {}", stopwatch);
			return UploadChunkContainer.addFileHash(chunk, hash);
		} catch (IOException ex) {
			LOG.error(String.format("Can't calculate hash for file: %s", chunk.getFileDto().getAbsolutePath()), ex);
		}
		LOG.warn("Returning EMPTY from UploadFileHasher");
		return UploadChunkContainer.EMPTY;
	}

	@Override
	public MessageDigest getMessageDigest() {
		return md;
	}

	@Override
	public FilesIOService getFilesIOService() {
		return filesIOService;
	}
}
