package pl.pamsoft.imapcloud.services.download;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.common.FileHasher;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.function.Function;

public class DownloadFileHasher implements Function<DownloadChunkContainer, DownloadChunkContainer>, FileHasher {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadFileHasher.class);

	private MessageDigest md;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public DownloadFileHasher(MessageDigest messageDigest, Statistics statistics, PerformanceDataService performanceDataService) {
		this.md = messageDigest;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		LOG.debug("Hashing file {}", dcc.getChunkToDownload().getOwnerFile().getName());
		Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			String hash = hash(DestFileUtils.generateFilePath(dcc).toFile());
			statistics.add(StatisticType.FILE_HASH, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.FILE_HASH, stopwatch));
			LOG.debug("File hash generated in {}", stopwatch);
			return DownloadChunkContainer.addFileHash(dcc, hash);
		} catch (IOException ex) {
			LOG.error(String.format("Can't calculate hash for file: %s", dcc.getChunkToDownload().getOwnerFile().getName()), ex);
		}
		LOG.warn("Returning EMPTY from DownloadFileHasher");
		return DownloadChunkContainer.EMPTY;
	}

	@Override
	public MessageDigest getMessageDigest() {
		return md;
	}
}
