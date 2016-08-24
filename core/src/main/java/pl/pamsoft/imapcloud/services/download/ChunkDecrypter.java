package pl.pamsoft.imapcloud.services.download;

import com.jamonapi.Monitor;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.IOException;
import java.util.function.Function;

public class ChunkDecrypter implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkDecrypter.class);
	private CryptoService cs;
	private final PerformanceDataService performanceDataService;
	private PaddedBufferedBlockCipher decryptingCipher;
	private MonitoringHelper monitoringHelper;

	public ChunkDecrypter(CryptoService cryptoService, String key, PerformanceDataService performanceDataService, MonitoringHelper monitoringHelper) {
		this.cs = cryptoService;
		this.performanceDataService = performanceDataService;
		decryptingCipher = cs.getDecryptingCipher(ByteUtils.fromHexString(key));
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		String fileName = dcc.getChunkToDownload().getOwnerFile().getName();
		LOG.debug("Decrypting chunk {} of {}", dcc.getChunkToDownload().getChunkNumber(), fileName);
		try {
			Monitor monitor = monitoringHelper.start(Keys.DL_CHUNK_DECRYPTER);
			byte[] decrypted = cs.decrypt(decryptingCipher, dcc.getData());
			double lastVal = monitoringHelper.stop(monitor);
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_DECRYPTER, lastVal));
			LOG.debug("{} chunk decrypting in {} (size: {} -> {}",
				fileName, monitor, decrypted.length, dcc.getData().length);
			return DownloadChunkContainer.addData(dcc, decrypted);
		} catch (InvalidCipherTextException | IOException e) {
			LOG.error("Error decrypting chunk", e);
		}
		LOG.warn("Returning EMPTY from ChunkDecrypter");
		return DownloadChunkContainer.EMPTY;
	}
}
