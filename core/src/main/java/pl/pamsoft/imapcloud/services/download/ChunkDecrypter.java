package pl.pamsoft.imapcloud.services.download;

import com.google.common.base.Stopwatch;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.IOException;
import java.util.function.Function;

public class ChunkDecrypter implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkDecrypter.class);
	private CryptoService cs;
	private Statistics statistics;
	private final PerformanceDataService performanceDataService;
	private PaddedBufferedBlockCipher decryptingCipher;

	public ChunkDecrypter(CryptoService cryptoService, String key, Statistics statistics, PerformanceDataService performanceDataService) {
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
			statistics.add(StatisticType.CHUNK_DECRYPTER, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_DECRYPTER, stopwatch));
			LOG.debug("{} chunk decrypting in {} (size: {} -> {}",
				fileName, stopwatch, decrypted.length, dcc.getData().length);
			return DownloadChunkContainer.addData(dcc, decrypted);
		} catch (InvalidCipherTextException | IOException e) {
			LOG.error("Error decrypting chunk", e);
		}
		LOG.warn("Returning EMPTY from ChunkDecrypter");
		return DownloadChunkContainer.EMPTY;
	}
}
