package pl.pamsoft.imapcloud.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.function.Consumer;

import static javax.mail.Folder.HOLDS_MESSAGES;

public class ChunkSaver implements Consumer<byte[]> {

	public static final String IMAP_CLOUD_FOLDER_NAME = "IC";
	private GenericObjectPool<Store> connectionPool;

	public ChunkSaver(GenericObjectPool<Store> connectionPool) {
		this.connectionPool = connectionPool;
	}

	@Override
	public void accept(byte[] byteBuffer) {
		Store store = null;
		try {
			store = connectionPool.borrowObject();
			Folder folder = store.getFolder(IMAP_CLOUD_FOLDER_NAME);
			if (!folder.exists()) {
				folder.create(HOLDS_MESSAGES);
			}
			Message[] msg = {createMessage(byteBuffer)};
			folder.appendMessages(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != store) {
				connectionPool.returnObject(store);
			}
		}
	}

	private Message createMessage(byte[] byteBuffer) throws MessagingException {
		String htmlBody = "";
		Message msg = new MimeMessage(Session.getInstance(System.getProperties()));

		Multipart mp = new MimeMultipart();

		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		mp.addBodyPart(htmlPart);

		MimeBodyPart attachment = new MimeBodyPart();
		InputStream attachmentDataStream = new ByteArrayInputStream(byteBuffer);
		DataSource ds = new ByteArrayDataSource(byteBuffer, "application/octet-stream");
		attachment.setDataHandler(new DataHandler(ds));
		attachment.setFileName("manual.pdf");
		mp.addBodyPart(attachment);

		msg.setContent(mp);
		return msg;
	}
}
