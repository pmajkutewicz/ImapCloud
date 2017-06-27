package pl.pamsoft.imapcloud.imap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.mail.Folder.HOLDS_MESSAGES;
import static javax.mail.Folder.READ_ONLY;
import static javax.mail.Folder.READ_WRITE;

public class ImapChunkUploader implements ChunkUploader {

	private static final Logger LOG = LoggerFactory.getLogger(ImapChunkUploader.class);
	private static final int MAX_RETRIES = 20;

	private GenericObjectPool<Store> connectionPool;
	private AtomicInteger retryCounter = new AtomicInteger(0);

	public ImapChunkUploader(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public String upload(UploadChunkContainer dataChunk, Map<String, String> metadata) throws IOException {
		retryCounter.set(0);
		return retryLoop(dataChunk, metadata);
	}

	private String retryLoop(UploadChunkContainer dataChunk, Map<String, String> metadata) throws IOException {
		String uploadedResult = uploadInt(dataChunk, metadata);
		if (StringUtils.EMPTY.equals(uploadedResult) && MAX_RETRIES > retryCounter.getAndIncrement()) {
			retryLoop(dataChunk, metadata);
		}
		return uploadedResult;
	}

	private String uploadInt(UploadChunkContainer dataChunk, Map<String, String> metadata) throws IOException {
		Store store = null;
		try {
			store = connectionPool.borrowObject();
			printPoolStats(connectionPool);
			Folder destFolder = getFolder(store, dataChunk);
			destFolder.open(READ_WRITE);
			Message message = createMessage(dataChunk, metadata);
			Message[] msg = {message};
			destFolder.appendMessages(msg);
			String[] header = message.getHeader("Message-ID");
			destFolder.close(IMAPUtils.NO_EXPUNGE);
			return header[0];
		} catch (Exception e) {
			try {
				connectionPool.invalidateObject(store);
				// do not return the object to the pool twice
				store = null;
			} catch (Exception e1) {
				LOG.error("Error invalidating", e1);
			}
			throw new IOException(e);
		} finally {
			if (null != store) {
				// make sure the object is returned to the pool
				connectionPool.returnObject(store);
				printPoolStats(connectionPool);
			}
		}
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	private Folder getFolder(Store store, UploadChunkContainer ucc) throws MessagingException {
		String imapPath = IMAPUtils.createFolderName(ucc);
		return createFolderIfDoesntExist(store, imapPath);
	}

	private Folder createFolderIfDoesntExist(Store store, String path) throws MessagingException {
		Folder folder = store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME).getFolder(path);
		if (!folder.exists()) {
			boolean result = folder.create(HOLDS_MESSAGES);
			folder.open(READ_ONLY);
			folder.close(IMAPUtils.NO_EXPUNGE);
			LOG.debug("Folder {} crated?: {}", path, result);
		}
		return folder;
	}

	private Message createMessage(UploadChunkContainer dataChunk, Map<String, String> metadata) throws MessagingException, IOException {
		String htmlBody = "";
		Message msg = new CustomMessageIdMimeMessage(Session.getInstance(System.getProperties()));

		Multipart mp = new MimeMultipart();
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		mp.addBodyPart(htmlPart);

		MimeBodyPart attachment = new MimeBodyPart();
		DataSource ds = new ByteArrayDataSource(dataChunk.getData(), "application/octet-stream");
		attachment.setDataHandler(new DataHandler(ds));
		String fileName = createFileName(dataChunk.getFileUniqueId(), dataChunk.getChunkNumber());
		attachment.setFileName(fileName);
		attachment.setDisposition(Part.ATTACHMENT);
		mp.addBodyPart(attachment);
		msg.setFrom(new InternetAddress("ic@127.0.0.1", "IMAPCloud"));
		msg.setContent(mp);
		msg.setSubject(fileName);
		for (Map.Entry<String, String> entry : metadata.entrySet()) {
			msg.setHeader(entry.getKey(), entry.getValue());
		}
		return msg;
	}

	private String createFileName(String fileName, int partNumber) {
		return String.format("%s.%05d", fileName, partNumber);
	}

	private void printPoolStats(GenericObjectPool<Store> pool){
		LOG.info("Stats start");
		LOG.info("Poolid: {}", pool.hashCode());
		LOG.info("getNumActive: {}", pool.getNumActive());
		LOG.info("getNumIdle: {}", pool.getNumIdle());
		LOG.info("Stats end");
	}
}
