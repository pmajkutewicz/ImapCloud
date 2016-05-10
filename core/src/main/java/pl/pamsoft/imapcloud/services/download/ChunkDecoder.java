package pl.pamsoft.imapcloud.services.download;

import com.google.common.base.Stopwatch;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.IOException;
import java.util.Base64;
import java.util.function.Function;

public class ChunkDecoder implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkDecoder.class);
	private CryptoService cs;
	private Statistics statistics;
	private final PerformanceDataService performanceDataService;
	private PaddedBufferedBlockCipher decryptingCipher;

	public ChunkDecoder(CryptoService cryptoService, String key, Statistics statistics, PerformanceDataService performanceDataService) {
		this.cs = cryptoService;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
		decryptingCipher = cs.getDecryptingCipher(ByteUtils.fromHexString(key));
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		String fileName = dcc.getChunkToDownload().getOwnerFile().getName();
		LOG.debug("Decrypting chunk {} of {}", dcc.getChunkToDownload().getChunkNumber(), fileName);
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			byte[] decrypted = cs.decrypt(decryptingCipher, dcc.getData());
			statistics.add(StatisticType.CHUNK_DECODER, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_DECODER, stopwatch));
			LOG.debug("{} chunk decrypting in {} (size: {} -> {}",
				fileName, stopwatch, decrypted.length, dcc.getData().length);
			return DownloadChunkContainer.addData(dcc, decrypted);
		} catch (InvalidCipherTextException | IOException e) {
			LOG.error("Error decrypting chunk", e);
		}
		LOG.warn("Returning EMPTY from ChunkDecoder");
		return DownloadChunkContainer.EMPTY;
	}
}