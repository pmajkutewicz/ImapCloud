package pl.pamsoft.imapcloud.imap;

import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static javax.mail.Folder.HOLDS_MESSAGES;
import static javax.mail.Folder.READ_ONLY;
import static javax.mail.Folder.READ_WRITE;

public class ChunkSaver implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ChunkSaver.class);

	private GenericObjectPool<Store> connectionPool;
	private final CryptoService cs;
	private Statistics statistics;
	private PerformanceDataService performanceDataService;
	private PaddedBufferedBlockCipher encryptingCipher;

	public ChunkSaver(GenericObjectPool<Store> connectionPool, CryptoService cryptoService, String cryptoKey, Statistics statistics, PerformanceDataService performanceDataService) {
		this.connectionPool = connectionPool;
		this.cs = cryptoService;
		encryptingCipher = cs.getEncryptingCipher(ByteUtils.fromHexString(cryptoKey));
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
			Folder destFolder = getFolder(store, dataChunk);
			destFolder.open(READ_WRITE);
			Message message = createMessage(dataChunk);
			Message[] msg = {message};
			destFolder.appendMessages(msg);
			String[] header = message.getHeader("Message-ID");
			destFolder.close(IMAPUtils.NO_EXPUNGE);
			statistics.add(StatisticType.CHUNK_SAVER, stopwatch.stop());
			performanceDataService.broadcast(new PerformanceDataEvent(StatisticType.CHUNK_SAVER, stopwatch));
			LOG.debug("Chunk saved in {}", stopwatch);
			return UploadChunkContainer.addMessageId(dataChunk, header[0]);
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
				printPoolStats(connectionPool);
			}
		}
		LOG.warn("Returning EMPTY from ChunkSaver");
		return UploadChunkContainer.EMPTY;
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

	private Message createMessage(UploadChunkContainer dataChunk) throws MessagingException, UnsupportedEncodingException {
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
		msg.setHeader("IC-ChunkNumber", String.valueOf(dataChunk.getChunkNumber()));
		msg.setHeader("IC-ChunkId", String.valueOf(dataChunk.getFileChunkUniqueId()));
		msg.setHeader("IC-FileId", String.valueOf(dataChunk.getFileUniqueId()));
		msg.setHeader("IC-FileName", encrypt(dataChunk.getFileDto().getName()));
		msg.setHeader("IC-FilePath", encrypt(dataChunk.getFileDto().getAbsolutePath()));
		msg.setHeader("IC-FileHash", dataChunk.getFileHash());
		return msg;
	}

	private  String encrypt(String toEncrypt) throws MessagingException {
		try {
			return cs.encryptHex(encryptingCipher, toEncrypt.getBytes(StandardCharsets.UTF_8));
		} catch (IOException | InvalidCipherTextException e) {
			throw new MessagingException("Unable to encrypt string: " + toEncrypt, e);
		}
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
