package pl.pamsoft.imapcloud.integration;

import com.icegreen.greenmail.imap.ImapHandlerImpl;
import com.icegreen.greenmail.imap.ImapRequestHandler;
import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.imap.commands.AppendCommand;
import com.icegreen.greenmail.imap.commands.ImapCommand;
import com.icegreen.greenmail.imap.commands.ImapCommandFactory;
import com.icegreen.greenmail.imap.commands.parsers.AppendCommandParser;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.requests.Encryption;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.testng.Assert.assertFalse;

public class UploadRestControllerNegativeIT extends AbstractIntegrationTest {

	private static final int ONE_MIB = 1024 * 1024 * 10;
	private UploadsRestClient uploadsRestClient;
	private AccountRestClient accountRestClient;
	private Common common;
	private GreenMail greenMail;
	private String username = RandomStringUtils.randomAlphabetic(10);
	private String pass = RandomStringUtils.randomAlphabetic(10);
	@Autowired
	private FileRepository fileRepository;
	private static AtomicInteger counter = new AtomicInteger(0);

	@BeforeClass
	public void init() {
		uploadsRestClient = new UploadsRestClient(getEndpoint(), "user", getPassword());
		accountRestClient = new AccountRestClient(getEndpoint(), "user", getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
		setupGreenMail();
	}

	@Test
	public void shouldUploadFile() throws Exception {
		AccountDto accountDto = createAccount();
		sendFile(accountDto);
	}

	private void sendFile(AccountDto accountDto) throws Exception {
		Path tempFile = TestUtils.createTempFile(ONE_MIB);
		String fileName = tempFile.getFileName().toString();

		Callable<Boolean> verifier = () -> {
			Collection<File> uploadedFiles = fileRepository.findAll();
			return isNotEmpty(uploadedFiles) ? uploadedFiles.stream().filter(File::isCompleted).anyMatch(f -> fileName.equals(f.getName())) : Boolean.FALSE;
		};
		assertFalse(verifier.call(), String.format("File %s already exists", fileName));

		List<FileDto> files = Collections.singletonList(new FileDto(fileName, tempFile.toAbsolutePath().toString(), FileDto.FileType.FILE, 8525172L));
		uploadsRestClient.startUpload(files, accountDto, Encryption.ON, new RequestCallback<Void>() {
			@Override
			public void onFailure(IOException e) {
				AssertJUnit.fail("Error starting upload.");
			}

			@Override
			public void onSuccess(Void data) throws IOException {

			}
		});

		//validate retries - check greenmail messages that we are sending same chunk
//		await().atMost(10, SECONDS).until(verifier, equalTo(true));
//		assertFalse(verifier.call());
	}

	private AccountDto createAccount() throws IOException, InterruptedException {

		return common.shouldCreateAccount(username, pass, "key", String.format("%s@localhost:23784", username), "imap");
	}


	private void setupGreenMail() {
		//Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
		ServerSetup setup = new ServerSetup(23784, "localhost", ServerSetup.PROTOCOL_IMAP).withImapHandler(new MyImapHandler());
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
			commands.put(AppendCommand.NAME, MyAppendCommand.class);
		}
	}

	public static class MyAppendCommand extends AppendCommand {
		private AppendCommandParser appendCommandParser = new AppendCommandParser();

		@Override
		public void doProcess(ImapRequestLineReader request, ImapResponse response, ImapSession session) throws ProtocolException, FolderException {
			if (counter.getAndIncrement() > 1) {
				appendCommandParser.mailbox(request);
				appendCommandParser.optionalAppendFlags(request);
				appendCommandParser.optionalDateTime(request);
				appendCommandParser.mimeMessage(request);
				appendCommandParser.endLine(request);
				throw new FolderException("Server exception");
			} else {
				super.doProcess(request, response, session);
			}
		}
	}
}
