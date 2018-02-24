package pl.pamsoft.imapcloud.integration.greenmail;

import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.imap.commands.AppendCommand;
import com.icegreen.greenmail.imap.commands.parsers.AppendCommandParser;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.MailFolder;

import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class CapacityToHighAppendCommand extends AppendCommand {
	private static final int ONE_MB = 1 * 1024 * 1024;
	private AppendCommandParser appendCommandParser = new AppendCommandParser();

	@Override
	public void doProcess(ImapRequestLineReader request, ImapResponse response, ImapSession session) throws ProtocolException, FolderException {
		String mailboxName = appendCommandParser.mailbox(request);
		Flags flags = appendCommandParser.optionalAppendFlags(request);
		if (flags == null) {
			flags = new Flags();
		}
		Date receivedDate = appendCommandParser.optionalDateTime(request);
		if (receivedDate == null) {
			receivedDate = new Date();
		}
		MimeMessage message = appendCommandParser.mimeMessage(request);
		appendCommandParser.endLine(request);

		MailFolder folder;
		try {
			folder = getMailbox(mailboxName, session, true);
		} catch (FolderException e) {
			e.setResponseCode("TRYCREATE");
			throw e;
		}

		long uid = folder.appendMessage(message, flags, receivedDate);

		try {
			if (message.getSize() > ONE_MB) {
				throw new FolderException("Failed");
			}
		} catch (MessagingException e) {
			throw new FolderException(e);
		}
		session.unsolicitedResponses(response);
		response.commandComplete(this, "APPENDUID" + SP + folder.getUidValidity() + SP + uid);
	}
}
