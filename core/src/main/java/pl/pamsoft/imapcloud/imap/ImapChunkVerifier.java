package pl.pamsoft.imapcloud.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkVerifier;
import pl.pamsoft.imapcloud.services.containers.VerifyChunkContainer;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import java.io.IOException;

public class ImapChunkVerifier implements ChunkVerifier {

	private static final Logger LOG = LoggerFactory.getLogger(ImapChunkVerifier.class);

	private final GenericObjectPool<Store> connectionPool;

	public ImapChunkVerifier(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public boolean verify(VerifyChunkContainer vcc) throws IOException {
		Store store = null;
		try {
			store = connectionPool.borrowObject();
			String folderName = IMAPUtils.createFolderName(vcc.getFileHash());
			Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
			folder.open(Folder.READ_ONLY);

			Message[] search = folder.search(new MessageIdSearchTerm(vcc.getStoredChunkId()));
			boolean chunkExists = search.length == 1;

			folder.close(IMAPUtils.NO_EXPUNGE);
			return chunkExists;
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
