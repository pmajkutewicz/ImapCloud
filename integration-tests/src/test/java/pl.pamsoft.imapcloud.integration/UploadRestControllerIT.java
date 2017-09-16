package pl.pamsoft.imapcloud.integration;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import java.nio.file.Files;
import java.nio.file.Path;

public class UploadRestControllerIT extends AbstractIntegrationTest {

	private static final int ONE_MIB = 1024 * 1024 * 1024;
	private UploadsRestClient uploadsRestClient;
	private AccountRestClient accountRestClient;
	private Common common;

	@Autowired
	private FileRepository fileRepository;

	@BeforeClass
	public void init() {
		OGlobalConfiguration.DISK_CACHE_SIZE.setValue(1000);
		uploadsRestClient = new UploadsRestClient(getEndpoint(), "user", getPassword());
		accountRestClient = new AccountRestClient(getEndpoint(), "user", getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
	}

	@Test
	public void shouldUploadFile() throws Exception {
		Path uploadedFile = common.shouldUploadFile(uploadsRestClient, fileRepository, ONE_MIB);
		Files.delete(uploadedFile);
	}

}
