package pl.pamsoft.imapcloud.services.upload;

import com.jamonapi.Monitor;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;
import pl.pamsoft.imapcloud.storage.imap.MessageHeaders;
import pl.pamsoft.imapcloud.utils.GitStatsUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ChunkUploaderFacade implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkUploaderFacade.class);
	private static final int THOUSAND = 1000;

	private final CryptoService cs;
	private ChunkUploader uploader;
	private GitStatsUtil gitStatsUtil;
	private PaddedBufferedBlockCipher encryptingCipher;
	private MonitoringHelper monitoringHelper;

	public ChunkUploaderFacade(ChunkUploader uploader, CryptoService cryptoService, String cryptoKey, GitStatsUtil gitStatsUtil, MonitoringHelper monitoringHelper) {
		this.uploader = uploader;
		this.cs = cryptoService;
		encryptingCipher = cs.getEncryptingCipher(ByteUtils.fromHexString(cryptoKey));
		this.gitStatsUtil = gitStatsUtil;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer dataChunk) {
		try {
			LOG.info("Uploading chunk {} of {}", dataChunk.getChunkNumber(), dataChunk.getFileDto().getName());
			Monitor monitor = monitoringHelper.start(Keys.UL_CHUNK_SAVER);

			String messageId = uploader.upload(dataChunk, createMetadata(dataChunk));

			double lastVal = monitoringHelper.stop(monitor);
			LOG.info("Chunk saved in {} ms", lastVal);
			monitor(lastVal, dataChunk.getChunkSize());

			return UploadChunkContainer.addMessageId(dataChunk, messageId);
		} catch (Exception e) {
			LOG.error("Error in stream", e);
		}
		LOG.warn("Returning EMPTY from ChunkUploaderFacade");
		return UploadChunkContainer.EMPTY;
	}

	private void monitor(double elapsedMs, long chunkSize) {
		monitoringHelper.add(Keys.IMAP_THROUGHPUT, ((double) chunkSize / elapsedMs) * THOUSAND);
	}

	@SuppressWarnings("PMD.EmptyCatchBlock")
	private Map<String, String> createMetadata(UploadChunkContainer dataChunk) throws IOException {
		Map<String, String> metadata = new HashMap<>();
		addMeta(metadata, MessageHeaders.ChunkNumber, String.valueOf(dataChunk.getChunkNumber()));
		addMeta(metadata, MessageHeaders.ChunkId, String.valueOf(dataChunk.getFileChunkUniqueId()));
		addMeta(metadata, MessageHeaders.ChunkHash, String.valueOf(dataChunk.getChunkHash()));
		addMeta(metadata, MessageHeaders.ChunkSize, String.valueOf(dataChunk.getChunkSize()));
		addMeta(metadata, MessageHeaders.ChunkEncrypted, String.valueOf(dataChunk.isEncrypted()));
		addMeta(metadata, MessageHeaders.LastChunk, String.valueOf(dataChunk.isLastChunk()));
		addMeta(metadata, MessageHeaders.FileId, String.valueOf(dataChunk.getFileUniqueId()));
		addMeta(metadata, MessageHeaders.FileName, encrypt(dataChunk.getFileDto().getName()));
		addMeta(metadata, MessageHeaders.FilePath, encrypt(dataChunk.getFileDto().getAbsolutePath()));
		addMeta(metadata, MessageHeaders.FileHash, dataChunk.getFileHash());
		try {
			addMeta(metadata, MessageHeaders.MagicNumber, gitStatsUtil.getGitRepositoryState().getCommitId());
		} catch (NullPointerException ignored) {

		}
		return metadata;
	}

	private  String encrypt(String toEncrypt) throws IOException {
		try {
			return cs.encryptHex(encryptingCipher, toEncrypt.getBytes(StandardCharsets.UTF_8));
		} catch (IOException | InvalidCipherTextException e) {
			throw new IOException("Unable to encrypt string: " + toEncrypt, e);
		}
	}

	private void addMeta(Map<String, String> metadata, MessageHeaders header, String value) {
		metadata.put(header.toString(), value);
	}
}
