package pl.pamsoft.imapcloud.storage.imap;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;

public class CustomMessageIdMimeMessage extends MimeMessage {
	public CustomMessageIdMimeMessage(Session session) {
		super(session);
	}

	public CustomMessageIdMimeMessage(Session session, InputStream is) throws MessagingException {
		super(session, is);
	}

	public CustomMessageIdMimeMessage(MimeMessage source) throws MessagingException {
		super(source);
	}

	protected CustomMessageIdMimeMessage(Folder folder, int msgnum) {
		super(folder, msgnum);
	}

	protected CustomMessageIdMimeMessage(Folder folder, InputStream is, int msgnum) throws MessagingException {
		super(folder, is, msgnum);
	}

	protected CustomMessageIdMimeMessage(Folder folder, InternetHeaders headers, byte[] content, int msgnum) throws MessagingException {
		super(folder, headers, content, msgnum);
	}

	protected void updateMessageID() throws MessagingException {
		String suffix;
		InternetAddress addr = InternetAddress.getLocalAddress(session);
		if (addr != null) {
			suffix = addr.getAddress();
		} else {
			suffix = "IC@localhost"; // worst-case default
		}
		setHeader("Message-ID", '<' + getSubject() + '.' + System.currentTimeMillis() + '.' + "IC" + '.' + suffix + '>');
	}
}
