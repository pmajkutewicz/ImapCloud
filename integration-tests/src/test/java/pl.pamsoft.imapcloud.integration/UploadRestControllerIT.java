package pl.pamsoft.imapcloud.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import java.nio.file.Files;
import java.nio.file.Path;

class UploadRestControllerIT extends AbstractIntegrationTest {

	private static final int ONE_MIB = 1024 * 1024;
	private UploadsRestClient uploadsRestClient;
	private AccountRestClient accountRestClient;
	private Common common;

	@Autowired
	private FileRepository fileRepository;

	@BeforeAll
	void init() {
		uploadsRestClient = new UploadsRestClient(getEndpoint(), getUsername(), getPassword());
		accountRestClient = new AccountRestClient(getEndpoint(), getUsername(), getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
	}

	@Test
	void shouldUploadFile() throws Exception {
		Path uploadedFile = common.shouldUploadFile(uploadsRestClient, fileRepository, ONE_MIB);
		Files.delete(uploadedFile);
	}

}
