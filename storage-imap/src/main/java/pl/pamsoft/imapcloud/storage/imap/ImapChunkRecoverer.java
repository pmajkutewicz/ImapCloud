package pl.pamsoft.imapcloud.storage.imap;

import com.sun.mail.imap.IMAPFolder.FetchProfileItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkRecoverer;
import pl.pamsoft.imapcloud.api.containers.RecoveryChunkContainer;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ImapChunkRecoverer implements ChunkRecoverer {

	private static final Logger LOG = LoggerFactory.getLogger(ImapChunkRecoverer.class);
	private final GenericObjectPool<Store> connectionPool;
	private List<String> requiredHeaders;

	//TODO: Add once again TasksProgressService tasksProgressService, Map<String, TaskProgress> taskProgressMap - see ChunkRecovery.class
	public ImapChunkRecoverer(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
		this.requiredHeaders = Stream.of(MessageHeaders.values()).map(MessageHeaders::toString).collect(toList());
		this.requiredHeaders.add("Message-ID");
	}

	@Override
	public List<Map<String, String>> recover(RecoveryChunkContainer rcc) throws IOException {
		Store store = null;
		try {
			store = connectionPool.borrowObject();
			Folder mainFolder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME);
			Folder[] list = mainFolder.list();
			List<Map<String, String>> result = new ArrayList<>();
			for (Folder folder : list) {
				LOG.debug("Processing {}", folder.getFullName());
				result.addAll(processFolder(folder));
			}
			return result;
		} catch (Exception e) {
			try {
				connectionPool.invalidateObject(store);
			} catch (Exception e1) {
				LOG.error("Error invalidating", e1);
			}
			throw new IOException(e);
		} finally {
			if (null != store) {
				connectionPool.returnObject(store);
			}
		}
	}

	private List<Map<String, String>> processFolder(Folder folder) throws MessagingException {
		LOG.debug("Opening: {}, has {} messages", folder.getFullName(), folder.getMessageCount());
		folder.open(Folder.READ_ONLY);
		Message[] messages = folder.getMessages();
		FetchProfile fetchProfile = new FetchProfile();
		fetchProfile.add(FetchProfileItem.HEADERS);
		fetchProfile.add(FetchProfileItem.ENVELOPE);
		fetchProfile.add(FetchProfileItem.CONTENT_INFO);
		requiredHeaders.forEach(fetchProfile::add);
		folder.fetch(messages, fetchProfile);
		List<Map<String, String>> result = processMessages(messages);
		folder.close(IMAPUtils.NO_EXPUNGE);
		return result;
	}

	private List<Map<String, String>> processMessages(Message[] messages) throws MessagingException {
		List<Map<String, String>> result = new ArrayList<>(messages.length);
		for (Message message : messages) {
			result.add(processMessage(message));
		}
		return result;
	}

	private Map<String, String> processMessage(Message message) throws MessagingException {
		Map<String, String> headers = getHeaders(message);
		headers.put("size", String.valueOf(message.getSize()));
		return headers;
	}

	@SuppressFBWarnings("CLI_CONSTANT_LIST_INDEX")
	private Map<String, String> getHeaders(Message message) throws MessagingException {
		Map<String, String> result = new HashMap<>();
		for (String header : requiredHeaders) {
			String value = message.getHeader(header)[0];
			result.put(header, value);
			LOG.trace("key: {}, value: {}", header, value);
		}
		return result;
	}
}
