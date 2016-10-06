package pl.pamsoft.imapcloud.imap;

import com.jamonapi.Monitor;
import com.sun.mail.imap.IMAPFolder.FetchProfileItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ChunkRecovery implements Function<RecoveryChunkContainer, RecoveryChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkRecovery.class);
	private final GenericObjectPool<Store> connectionPool;
	private final PerformanceDataService performanceDataService;
	private MonitoringHelper monitoringHelper;
	private List<String> requiredHeaders;

	private final Map<String, File> fileMap = new HashMap<>();
	private final Map<String, List<FileChunk>> fileChunkMap = new HashMap<>();

	public ChunkRecovery(GenericObjectPool<Store> connectionPool, PerformanceDataService performanceDataService, MonitoringHelper monitoringHelper) {
		this.connectionPool = connectionPool;
		this.performanceDataService = performanceDataService;
		this.monitoringHelper = monitoringHelper;
		this.requiredHeaders = Stream.of(MessageHeaders.values()).map(MessageHeaders::toString).collect(toList());
		this.requiredHeaders.add("Message-ID");
	}

	@Override
	public RecoveryChunkContainer apply(RecoveryChunkContainer rcc) {
		Store store = null;
		try {
			LOG.info("Recovering chunks");
			Monitor monitor = monitoringHelper.start(Keys.RE_CHUNK_RECOVERY);
			store = connectionPool.borrowObject();
			Folder mainFolder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME);
			Folder[] list = mainFolder.list();
			for (Folder folder : list) {
				processFolder(folder);
			}
			determineSizeAndCompleteness();
			double lastVal = monitoringHelper.stop(monitor);
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_RECOVERY, lastVal));
			LOG.debug("Recovered chunks in {}", lastVal);
			return RecoveryChunkContainer.addRecoveredFilesData(rcc, fileMap, fileChunkMap);
		} catch (Exception e) {
			LOG.error("Error in stream", e);
			try {
				connectionPool.invalidateObject(store);
			} catch (Exception e1) {
				LOG.error("Error invalidating", e1);
			}
		} finally {
			if (null != store) {
				connectionPool.returnObject(store);
			}
		}
		return RecoveryChunkContainer.EMPTY;
	}

	private void determineSizeAndCompleteness() {
		Collection<List<FileChunk>> values = fileChunkMap.values();
		for (List<FileChunk> fileChunks : values) {
			fileChunks.sort((o1, o2) -> Integer.compare(o1.getChunkNumber(), o2.getChunkNumber()));
			int nbOfChunks = fileChunks.size();
			FileChunk lastChunk = fileChunks.get(nbOfChunks - 1);
			File file = lastChunk.getOwnerFile();
			file.setCompleted(nbOfChunks == lastChunk.getChunkNumber() && lastChunk.isLastChunk());
			file.setSize(fileChunks.stream().mapToLong(FileChunk::getSize).sum());
		}
	}

	private void processFolder(Folder folder) throws MessagingException {
		LOG.debug("Opening: {}, has {} messages", folder.getFullName(), folder.getMessageCount());
		folder.open(Folder.READ_ONLY);
		Message[] messages = folder.getMessages();
		FetchProfile fetchProfile = new FetchProfile();
		fetchProfile.add(FetchProfileItem.HEADERS);
		fetchProfile.add(FetchProfileItem.ENVELOPE);
		fetchProfile.add(FetchProfileItem.CONTENT_INFO);
		requiredHeaders.forEach(fetchProfile::add);
		folder.fetch(messages, fetchProfile);
		processMessages(messages);
		folder.close(IMAPUtils.NO_EXPUNGE);
	}

	private void processMessages(Message[] messages) throws MessagingException {
		for (Message message : messages) {
			processMessage(message);
		}
	}

	private void processMessage(Message message) throws MessagingException {
		Map<String, String> headers = getHeaders(message);
		headers.put("size", String.valueOf(message.getSize()));
		File file = headersToFile(headers);
		fileMap.putIfAbsent(file.getFileUniqueId(), file);
		FileChunk fileChunk = headersToFileChunk(headers);
		List<FileChunk> fileChunks = fileChunkMap.get(file.getFileUniqueId());

		if (null != fileChunks) {
			fileChunks.add(fileChunk);
		} else {
			ArrayList<FileChunk> chunkList = new ArrayList<>();
			chunkList.add(fileChunk);
			fileChunkMap.put(file.getFileUniqueId(), chunkList);
		}
	}

	@SuppressFBWarnings("CLI_CONSTANT_LIST_INDEX")
	private Map<String, String> getHeaders(Message message) throws MessagingException {
		Map<String, String> result = new HashMap<>();
		for (String header: requiredHeaders) {
			String value = message.getHeader(header)[0];
			result.put(header, value);
			LOG.trace("key: {}, value: {}", header, value);
		}
		return result;
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
		fileChunk.setSize(Long.valueOf(headers.get("size")));
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
