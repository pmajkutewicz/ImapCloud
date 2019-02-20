package pl.pamsoft.imapcloud.integration;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.requests.Encryption;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;
import pl.pamsoft.imapcloud.storage.testing.TestingAccountService;
import pl.pamsoft.imapcloud.storage.testing.TestingChunkUploader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ConcurrentChunkUploadIT extends AbstractIntegrationTest {

	private static final int FIVE_MB = 5 * 1024 * 1024;
	private UploadsRestClient uploadsRestClient;
	private AccountRestClient accountRestClient;
	private Common common;

	@Autowired
	private TestingAccountService testingAccountService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FileRepository fileRepository;

	@BeforeAll
	void init() {
		uploadsRestClient = new UploadsRestClient(getEndpoint(), getUsername(), getPassword());
		accountRestClient = new AccountRestClient(getEndpoint(), getUsername(), getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
	}

	@Test
	void shouldUseAllAvailableConcurrentConnections() throws Exception {

		String username = RandomStringUtils.randomAlphabetic(10);
		AccountDto accountDto = common.shouldCreateAccount(username, "test", "key", String.format("%s@localhost_testing", username), "ic-testing");
		assertNotNull(accountDto);

		TestingChunkUploader chunkUploader = (TestingChunkUploader) testingAccountService.getChunkUploader(accountRepository.getById(accountDto.getId()));

		assertEquals(0, chunkUploader.getCounter().get());

		List<FileDto> filesToUpload = createFiles();
		uploadsRestClient.startUpload(filesToUpload, accountDto, Encryption.ON, new RequestCallback<Void>() {
			@Override
			public void onFailure(IOException e) {
				fail("Error starting upload.");
			}

			@Override
			public void onSuccess(Void data) throws IOException {

			}
		});

		Callable<Boolean> verifier = () -> chunkUploader.getCounter().get() == 8;
		with().pollDelay(2, SECONDS).await().atMost(2, MINUTES).until(verifier, equalTo(true));
		assertTrue(verifier.call());
	}

	private List<FileDto> createFiles() throws IOException {
		List<FileDto> fileDtos = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Path tempFile = TestUtils.createTempFile(FIVE_MB);
			String fileName = tempFile.getFileName().toString();
			fileDtos.add(new FileDto(fileName, tempFile.toAbsolutePath().toString(), FileDto.FileType.FILE, 8525172L));
		}
		return fileDtos;
	}
}
