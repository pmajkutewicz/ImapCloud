package pl.pamsoft.imapcloud.integration;

import com.icegreen.greenmail.imap.ImapHandlerImpl;
import com.icegreen.greenmail.imap.ImapRequestHandler;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.imap.commands.AppendCommand;
import com.icegreen.greenmail.imap.commands.ImapCommand;
import com.icegreen.greenmail.imap.commands.ImapCommandFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.rest.AccountRestClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

abstract class AbstractGreenMailIntegrationTest extends AbstractIntegrationTest {
	protected AccountRestClient accountRestClient;
	protected Common common;
	protected GreenMail greenMail;
	protected AccountDto testAccountDto;
	private String username = RandomStringUtils.randomAlphabetic(10);
	private String pass = RandomStringUtils.randomAlphabetic(10);

	@BeforeAll
	void init() throws IOException, InterruptedException {
		accountRestClient = new AccountRestClient(getEndpoint(), getUsername(), getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
		testAccountDto = common.shouldCreateAccount(username, pass, "key", String.format("%s@localhost:23784", username), "imap");
		setupGreenMail();
	}

	@AfterAll
	void shutDown() {
		if (null != greenMail) {
			greenMail.stop();
		}
	}

	private void setupGreenMail() {
		//Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
		ServerSetup setup = new ServerSetup(23784, "localhost", ServerSetup.PROTOCOL_IMAPS).withImapHandler(new MyImapHandler());
		greenMail = new GreenMail(ServerSetup.verbose(new ServerSetup[]{ setup}));
		greenMail.setUser(username, pass);
		greenMail.setQuotaSupported(false);
		greenMail.start();
	}

	class MyImapHandler extends ImapHandlerImpl {
		MyImapHandler() {
			setRequestHandler(new MyImapRequestHandler());
		}
	}

	class MyImapRequestHandler extends ImapRequestHandler {
		MyImapRequestHandler() {
			setImapCommands(new MyImapCommandFactory());
		}

		@Override
		public boolean handleRequest(InputStream input, OutputStream output, ImapSession session) throws ProtocolException {
			return super.handleRequest(input, output, session);
		}
	}

	class MyImapCommandFactory extends ImapCommandFactory {
		public MyImapCommandFactory() {
			Map<String, Class<? extends ImapCommand>> commands = getImapCommands();
			commands.remove(AppendCommand.NAME);
			commands.put(AppendCommand.NAME, getAppendClass());
		}
	}

	abstract Class<? extends ImapCommand> getAppendClass();
}
