package pl.pamsoft.imapcloud.services.upload;

import com.google.common.base.Stopwatch;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.IOException;
import java.util.function.Function;

public class ChunkEncoder implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkEncoder.class);
	private CryptoService cs;
	private Statistics statistics;
	private final PerformanceDataService performanceDataService;
	private PaddedBufferedBlockCipher encryptingCipher;

	public ChunkEncoder(CryptoService cryptoService, String key, Statistics statistics, PerformanceDataService performanceDataService) {
		this.cs = cryptoService;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
		byte[] keyBytes = ByteUtils.fromHexString(key);
		encryptingCipher = cs.getEncryptingCipher(keyBytes);
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer uploadChunkContainer) {
		LOG.debug("Encrypting chunk {} of {}", uploadChunkContainer.getChunkNumber(), uploadChunkContainer.getFileDto().getName());
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			byte[] encrypted = cs.encrypt(encryptingCipher, uploadChunkContainer.getData());
			statistics.add(StatisticType.CHUNK_ENCODER, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_ENCODER, stopwatch));
			LOG.debug("{} chunk encrypted in {} (size: {} -> {}",
				uploadChunkContainer.getFileDto().getAbsolutePath(), stopwatch, uploadChunkContainer.getData().length, encrypted.length);
			return UploadChunkContainer.addEncryptedData(uploadChunkContainer, encrypted);
		} catch (InvalidCipherTextException | IOException e) {
			LOG.error("Error encrypting chunk", e);
		}
		LOG.warn("Returning EMPTY from ChunkEncoder");
		return UploadChunkContainer.EMPTY;
	}
}
