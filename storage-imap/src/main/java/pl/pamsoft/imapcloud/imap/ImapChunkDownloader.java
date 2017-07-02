package pl.pamsoft.imapcloud.imap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkDownloader;
import pl.pamsoft.imapcloud.api.containers.DownloadChunkContainer;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Store;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImapChunkDownloader implements ChunkDownloader {

	private static final Logger LOG = LoggerFactory.getLogger(ImapChunkDownloader.class);
	private static final int FIRST_MESSAGE = 0;
	private final GenericObjectPool<Store> connectionPool;

	public ImapChunkDownloader(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public byte[] download(DownloadChunkContainer dcc) throws IOException{
		Store store = null;
		try {
			store = connectionPool.borrowObject();
			String folderName = IMAPUtils.createFolderName(dcc.getExpectedFileHash());
			Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
			folder.open(Folder.READ_ONLY);

			Message[] search = folder.search(new MessageIdSearchTerm(dcc.getStorageChunkId()));
			byte[] attachment = getAttachment(search[FIRST_MESSAGE]);

			folder.close(IMAPUtils.NO_EXPUNGE);
			return attachment;
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

	@SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
	private byte[] getAttachment(Message message) throws IOException, MessagingException {
		Multipart multipart = (Multipart) message.getContent();
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
				!StringUtils.isNotBlank(bodyPart.getFileName())) {
				continue; // dealing with attachments only
			}
			InputStream is = bodyPart.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream(bodyPart.getSize());
			IOUtils.copyLarge(is, baos);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(baos);
			return baos.toByteArray();
		}
		throw new IOException("Missing attachment");
	}
}
