package pl.pamsoft.imapcloud.imap;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.common.StatisticType;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.websocket.PerformanceDataEvent;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.util.function.Function;

import static javax.mail.Folder.HOLDS_MESSAGES;
import static javax.mail.Folder.READ_ONLY;
import static javax.mail.Folder.READ_WRITE;

public class ChunkSaver implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkSaver.class);

	private static final String IMAP_CLOUD_FOLDER_NAME = "IC";
	private static final boolean NO_EXPUNGE = false;
	private GenericObjectPool<Store> connectionPool;
	private final CryptoService cs;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;

	public ChunkSaver(GenericObjectPool<Store> connectionPool, CryptoService cryptoService, Statistics statistics, PerformanceDataService performanceDataService) {
		this.connectionPool = connectionPool;
		this.cs = cryptoService;
		this.statistics = statistics;
		this.performanceDataService = performanceDataService;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer dataChunk) {
		Store store = null;
		try {
			LOG.info("Uploading chunk {} of {}", dataChunk.getChunkNumber(), dataChunk.getFileDto().getName());
			Stopwatch stopwatch = Stopwatch.createStarted();
			store = connectionPool.borrowObject();
			printPoolStats(connectionPool);
			Folder destFolder = getFolder(store, dataChunk.getFileDto().getAbsolutePath());
			destFolder.open(READ_WRITE);
			Message message = createMessage(dataChunk);
			Message[] msg = {message};
			destFolder.appendMessages(msg);
			String[] header = message.getHeader("Message-ID");
			destFolder.close(NO_EXPUNGE);
			statistics.add(StatisticType.CHUNK_SAVER, stopwatch);
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_SAVER, stopwatch));
			LOG.debug("Chunk saved in {}", stopwatch);
			return UploadChunkContainer.addMessageId(dataChunk, header[0]);
		} catch (Exception e) {
			e.printStackTrace();
			//TODO
		} finally {
			if (null != store) {
				connectionPool.returnObject(store);
				printPoolStats(connectionPool);
			}
		}
		LOG.warn("Returning EMPTY from ChunkSaver");
		return UploadChunkContainer.EMPTY;
	}

	private Folder getFolder(Store store, String absolutePathName) throws MessagingException {
		String imapPath = createFolderName(absolutePathName);
		return createFolderIfDoesntExist(store, imapPath);
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	private String createFolderName(String absoluteFilePath) {
		String absolutePath = new File(absoluteFilePath).getParent();
		return cs.rot13(absolutePath.replace(File.separator, ""));
	}

	private Folder createFolderIfDoesntExist(Store store, String path) throws MessagingException {
		Folder folder = store.getFolder(IMAP_CLOUD_FOLDER_NAME).getFolder(path);
		if (!folder.exists()) {
			boolean result = folder.create(HOLDS_MESSAGES);
			folder.open(READ_ONLY);
			folder.close(NO_EXPUNGE);
			LOG.debug("Folder {} crated?: {}", path, result);
		}
		return folder;
	}

	private Message createMessage(UploadChunkContainer dataChunk) throws MessagingException {
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
		msg.setContent(mp);
		msg.setSubject(fileName);
		msg.setHeader("IC-ChunkNumber", String.valueOf(dataChunk.getChunkNumber()));
		msg.setHeader("IC-ChunkId", String.valueOf(dataChunk.getFileChunkUniqueId()));
		msg.setHeader("IC-FileId", String.valueOf(dataChunk.getFileUniqueId()));
		return msg;
	}

	private String createFileName(String fileName, int partNumber) {
		return String.format("%s.%04d", fileName, partNumber);
	}



	private void printPoolStats(GenericObjectPool<Store> pool){
		LOG.info("Stats start");
		LOG.info("Poolid: {}", pool.hashCode());
		LOG.info("getNumActive: {}", pool.getNumActive());
		LOG.info("getNumIdle: {}", pool.getNumIdle());
		LOG.info("Stats end");
	}
}
