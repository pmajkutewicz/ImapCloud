package pl.pamsoft.imapcloud.services.upload;

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
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import java.io.IOException;
import java.util.function.Function;

public class ChunkEncrypter implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkEncrypter.class);
	private CryptoService cs;
	private final PerformanceDataService performanceDataService;
	private PaddedBufferedBlockCipher encryptingCipher;
	private MonitoringHelper monitoringHelper;

	public ChunkEncrypter(CryptoService cryptoService, String key, PerformanceDataService performanceDataService, MonitoringHelper monitoringHelper) {
		this.cs = cryptoService;
		this.performanceDataService = performanceDataService;
		encryptingCipher = cs.getEncryptingCipher(ByteUtils.fromHexString(key));
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer uploadChunkContainer) {
		LOG.debug("Encrypting chunk {} of {}", uploadChunkContainer.getChunkNumber(), uploadChunkContainer.getFileDto().getName());
		try {
			Monitor monitor = monitoringHelper.start(Keys.UL_CHUNK_ENCRYPTER);
			byte[] encrypted = cs.encrypt(encryptingCipher, uploadChunkContainer.getData());
			double lastVal = monitoringHelper.stop(monitor);
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_ENCRYPTER, lastVal));
			LOG.debug("{} chunk encrypted in {} (size: {} -> {})",
				uploadChunkContainer.getFileDto().getAbsolutePath(), lastVal, uploadChunkContainer.getData().length, encrypted.length);
			return UploadChunkContainer.addEncryptedData(uploadChunkContainer, encrypted);
		} catch (InvalidCipherTextException | IOException e) {
			LOG.error("Error encrypting chunk", e);
		}
		LOG.warn("Returning EMPTY from ChunkEncrypter");
		return UploadChunkContainer.EMPTY;
	}
}
