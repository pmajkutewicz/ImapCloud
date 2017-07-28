package pl.pamsoft.imapcloud.services.recovery;

import com.jamonapi.Monitor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkRecoverer;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.RecoveryChunkContainer;
import pl.pamsoft.imapcloud.storage.imap.MessageHeaders;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ChunkRecovererFacade implements Function<RecoveryChunkContainer, RecoveryChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkRecovererFacade.class);
	private final ChunkRecoverer chunkRecoverer;
	private final MonitoringHelper monitoringHelper;
	private Map<String, File> fileMap = new HashMap<>();
	private Map<String, List<FileChunk>> fileChunkMap = new HashMap<>();

	public ChunkRecovererFacade(ChunkRecoverer chunkRecoverer, MonitoringHelper monitoringHelper) {
		this.chunkRecoverer = chunkRecoverer;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public RecoveryChunkContainer apply(RecoveryChunkContainer rcc) {
		try {
			LOG.info("Recovering chunks");
			Monitor monitor = monitoringHelper.start(Keys.RE_CHUNK_RECOVERY);

			List<Map<String, String>> recoveredChunks = chunkRecoverer.recover(rcc);
			postProcess(recoveredChunks);

			double lastVal = monitoringHelper.stop(monitor);
			LOG.info("Recovered chunks in {} ms", lastVal);
			return RecoveryChunkContainer.addRecoveredFilesData(rcc, fileMap, fileChunkMap);
		} catch (Exception e) {
			LOG.error("Error in stream", e);
		}

		LOG.warn("Returning EMPTY from ChunkRecovererFacade");
		return RecoveryChunkContainer.EMPTY;
	}

	private void postProcess(List<Map<String, String>> recoveredChunks) throws MessagingException {
		recoveredChunks.forEach(this::processMessage);
		determineSizeAndCompleteness();
	}

	private void determineSizeAndCompleteness() {
		Collection<List<FileChunk>> values = fileChunkMap.values();
		for (List<FileChunk> fileChunks : values) {
			fileChunks.sort(Comparator.comparingInt(FileChunk::getChunkNumber));
			int nbOfChunks = fileChunks.size();
			FileChunk lastChunk = fileChunks.get(nbOfChunks - 1);
			File file = lastChunk.getOwnerFile();
			file.setCompleted(nbOfChunks == lastChunk.getChunkNumber() && lastChunk.isLastChunk());
			file.setSize(fileChunks.stream().mapToLong(FileChunk::getOrgSize).sum());
		}
	}

	private void processMessage(Map<String, String> chunkHeaderMap) {
		File file = headersToFile(chunkHeaderMap);
		fileMap.putIfAbsent(file.getFileUniqueId(), file);
		FileChunk fileChunk = headersToFileChunk(chunkHeaderMap);
		List<FileChunk> fileChunks = fileChunkMap.get(file.getFileUniqueId());

		if (null != fileChunks) {
			fileChunks.add(fileChunk);
		} else {
			ArrayList<FileChunk> chunkList = new ArrayList<>();
			chunkList.add(fileChunk);
			fileChunkMap.put(file.getFileUniqueId(), chunkList);
		}
	}

	private File headersToFile(Map<String, String> headers) {
		File file = new File();
		file.setName(headers.get(MessageHeaders.FileName.toString()));
		file.setFileHash(headers.get(MessageHeaders.FileHash.toString()));
		file.setFileUniqueId(headers.get(MessageHeaders.FileId.toString()));
		file.setAbsolutePath(headers.get(MessageHeaders.FilePath.toString()));
		return file;
	}

	private FileChunk headersToFileChunk(Map<String, String> headers) {
		FileChunk fileChunk = new FileChunk();
		fileChunk.setOrgSize(Long.valueOf(headers.get(MessageHeaders.ChunkSize.toString())));
		fileChunk.setChunkExists(Boolean.TRUE);
		fileChunk.setChunkHash(headers.get(MessageHeaders.ChunkHash.toString()));
		fileChunk.setChunkNumber(Integer.parseInt(headers.get(MessageHeaders.ChunkNumber.toString())));
		fileChunk.setFileChunkUniqueId(headers.get(MessageHeaders.ChunkId.toString()));
		fileChunk.setLastChunk(Boolean.parseBoolean(headers.get(MessageHeaders.LastChunk.toString())));
		fileChunk.setOwnerFile(fileMap.get(headers.get(MessageHeaders.FileId.toString())));
		fileChunk.setMessageId(headers.get("Message-ID"));
		fileChunk.setLastVerifiedAt(new DateTime().getMillis());
		return fileChunk;
	}
}
