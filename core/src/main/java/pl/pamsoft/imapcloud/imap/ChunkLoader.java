package pl.pamsoft.imapcloud.imap;

import com.jamonapi.Monitor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.Keys;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

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
import java.util.function.Function;

public class ChunkLoader implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkLoader.class);
	private static final int FIRST_MESSAGE = 0;
	private final GenericObjectPool<Store> connectionPool;
	private MonitoringHelper monitoringHelper;

	public ChunkLoader(GenericObjectPool<Store> connectionPool, MonitoringHelper monitoringHelper) {
		this.connectionPool = connectionPool;
		this.monitoringHelper = monitoringHelper;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		Store store = null;
		try {
			FileChunk fileChunk = dcc.getChunkToDownload();
			LOG.info("Downloading chunk {} of {}", fileChunk.getChunkNumber(), fileChunk.getOwnerFile().getName());
			Monitor monitor = monitoringHelper.start(Keys.DL_CHUNK_LOADER);
			store = connectionPool.borrowObject();
			String folderName = IMAPUtils.createFolderName(fileChunk);
			Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(folderName);
			folder.open(Folder.READ_ONLY);

			Message[] search = folder.search(new MessageIdSearchTerm(fileChunk.getMessageId()));
			byte[] attachment = getAttachment(search[FIRST_MESSAGE]);

			folder.close(IMAPUtils.NO_EXPUNGE);
			double lastVal = monitoringHelper.stop(monitor);
			LOG.info("Chunk downloaded in {} ms", lastVal);
			return DownloadChunkContainer.addData(dcc, attachment);
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
		return DownloadChunkContainer.EMPTY;
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
