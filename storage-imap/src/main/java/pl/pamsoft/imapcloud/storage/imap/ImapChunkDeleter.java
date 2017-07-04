package pl.pamsoft.imapcloud.storage.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkDeleter;
import pl.pamsoft.imapcloud.api.containers.DeleteChunkContainer;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.search.HeaderTerm;
import java.io.IOException;

public class ImapChunkDeleter implements ChunkDeleter {

	private static final Logger LOG = LoggerFactory.getLogger(ImapChunkDeleter.class);

	private final GenericObjectPool<Store> connectionPool;

	public ImapChunkDeleter(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public boolean delete(DeleteChunkContainer dcc) throws IOException {
		Store store = null;
		try {
			store = connectionPool.borrowObject();
			String folderName = IMAPUtils.createFolderName(dcc.getFileHash());
			Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
			folder.open(Folder.READ_WRITE);

			Message[] messages = folder.search(new HeaderTerm(MessageHeaders.FileId.toString(), dcc.getFileUniqueId()));
			for (Message message : messages) {
				message.setFlag(Flags.Flag.DELETED, true);
			}
			folder.close(IMAPUtils.EXPUNGE);
			return true;
		} catch (Exception e) {
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
		return false;
	}
}
