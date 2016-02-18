package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.mbeans.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.security.MessageDigest;
import java.util.function.Function;

public class ChunkHasher implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkHasher.class);

	private MessageDigest md;
	private Statistics statistics;

	public ChunkHasher(MessageDigest messageDigest, Statistics statistics) {
		this.md = messageDigest;
		this.statistics = statistics;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer chunk) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		byte[] digest = md.digest(chunk.getData());
		String hash = String.format("%s", ByteUtils.toHexString(digest));
		statistics.add(StatisticType.CHUNK_HASH, stopwatch);
		LOG.debug("Hash generated in {}", stopwatch.stop());
		return UploadChunkContainer.addChunkHash(chunk, hash);
	}
}
