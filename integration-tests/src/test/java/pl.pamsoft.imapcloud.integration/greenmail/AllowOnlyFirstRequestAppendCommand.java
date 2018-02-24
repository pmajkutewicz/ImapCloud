package pl.pamsoft.imapcloud.integration.greenmail;

import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.imap.commands.AppendCommand;
import com.icegreen.greenmail.imap.commands.parsers.AppendCommandParser;
import com.icegreen.greenmail.store.FolderException;

import java.util.concurrent.atomic.AtomicInteger;

public class AllowOnlyFirstRequestAppendCommand extends AppendCommand {
	private static AtomicInteger counter = new AtomicInteger(1);
	private AppendCommandParser appendCommandParser = new AppendCommandParser();

	@Override
	public void doProcess(ImapRequestLineReader request, ImapResponse response, ImapSession session) throws ProtocolException, FolderException {
		if (counter.getAndIncrement() > 1) {
			appendCommandParser.mailbox(request);
			appendCommandParser.optionalAppendFlags(request);
			appendCommandParser.optionalDateTime(request);
			appendCommandParser.endLine(request);
			throw new FolderException("Failed");
		} else {
			super.doProcess(request, response, session);
		}
	}
}
