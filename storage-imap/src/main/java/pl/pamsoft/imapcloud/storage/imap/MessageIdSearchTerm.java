package pl.pamsoft.imapcloud.storage.imap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;

public class MessageIdSearchTerm extends SearchTerm {
	private String expectedId;

	MessageIdSearchTerm(String expectedId) {
		this.expectedId = expectedId;
	}

	@Override
	public boolean match(Message message) {
		try {
			String messageID = ((MimeMessage) message).getMessageID();
			if (messageID.contains(expectedId)) {
				return true;
			}
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
