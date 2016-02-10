package pl.pamsoft.imapcloud.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

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
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.function.Consumer;

import static javax.mail.Folder.HOLDS_MESSAGES;

public class ChunkSaver implements Consumer<UploadChunkContainer> {

	public static final String IMAP_CLOUD_FOLDER_NAME = "IC";
	private GenericObjectPool<Store> connectionPool;

	public ChunkSaver(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public void accept(UploadChunkContainer dataChunk) {
		Store store = null;
		try {
			store = connectionPool.borrowObject();
			Folder folder = store.getFolder(IMAP_CLOUD_FOLDER_NAME);
			if (!folder.exists()) {
				folder.create(HOLDS_MESSAGES);
			}
			Message[] msg = {createMessage(dataChunk)};
			folder.appendMessages(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != store) {
				connectionPool.returnObject(store);
			}
		}
	}

	private Message createMessage(UploadChunkContainer dataChunk) throws MessagingException {
		String htmlBody = "";
		Message msg = new MimeMessage(Session.getInstance(System.getProperties()));

		Multipart mp = new MimeMultipart();
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		mp.addBodyPart(htmlPart);

		MimeBodyPart attachment = new MimeBodyPart();
		DataSource ds = new ByteArrayDataSource(dataChunk.getData(), "application/octet-stream");
		attachment.setDataHandler(new DataHandler(ds));
		attachment.setFileName("manual.pdf");
		attachment.setDisposition(Part.ATTACHMENT);
		mp.addBodyPart(attachment);
		msg.setContent(mp);
		msg.setHeader("IC-ChunkNumber", String.valueOf(dataChunk.getChunkNumber()));
		return msg;
	}
}
