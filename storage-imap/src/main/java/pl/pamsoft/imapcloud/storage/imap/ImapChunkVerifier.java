package pl.pamsoft.imapcloud.storage.imap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sun.mail.imap.IMAPFolder;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkVerifier;
import pl.pamsoft.imapcloud.api.containers.VerifyChunkContainer;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ImapChunkVerifier implements ChunkVerifier {

	private static final Logger LOG = LoggerFactory.getLogger(ImapChunkVerifier.class);
	private static final int TEN = 10;

	private final GenericObjectPool<Store> connectionPool;

	private final Cache<String, Message[]> cache = CacheBuilder.newBuilder().expireAfterWrite(TEN, TimeUnit.MINUTES).build();

	private final Function<Message, String[]> extractMessageId = m -> {
		try {
			return m.getHeader("Message-Id");
		} catch (MessagingException e) {
			return new String[0];
		}
	};

	public ImapChunkVerifier(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public boolean verify(VerifyChunkContainer vcc) throws IOException {
		Store store = null;
		try {
			String folderName = IMAPUtils.createFolderName(vcc.getFileHash());
			Message[] cachedMessages = cache.getIfPresent(folderName);
			if (null != cachedMessages) {
				return Arrays.stream(cachedMessages).map(extractMessageId).flatMap(Arrays::stream).anyMatch(s -> s.equals(vcc.getStorageChunkId()));
			} else {
				store = connectionPool.borrowObject();
				// cache this for like 5 min
				Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
				folder.open(Folder.READ_ONLY);

				Message[] messages = folder.getMessages();
				FetchProfile fetchProfile = new FetchProfile();
				fetchProfile.add(IMAPFolder.FetchProfileItem.HEADERS);
				folder.fetch(messages, fetchProfile);
				cache.put(folderName, messages);

				MessageIdSearchTerm messageIdSearchTerm = new MessageIdSearchTerm(vcc.getStorageChunkId());
				boolean chunkExists = Arrays.stream(messages).anyMatch(messageIdSearchTerm::match);

				folder.close(IMAPUtils.NO_EXPUNGE);
				return chunkExists;
			}
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
}
