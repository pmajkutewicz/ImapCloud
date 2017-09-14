package pl.pamsoft.imapcloud.integration;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

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
		AccountDto accountDto = common.shouldCreateAccount("shouldUploadFile", "test", "key", "shouldUploadFile@localhost_tmp");
		Path tempFile = TestUtils.createTempFile(ONE_MIB);
		String fileName = tempFile.getFileName().toString();

		Callable<Boolean> verifier = () -> {
			Collection<File> uploadedFiles = fileRepository.findAll();
			// have to verify also isCompleted(), but it looks like some kind of caching issue and all services doesn't see updated value.
			return isNotEmpty(uploadedFiles) ? uploadedFiles.stream().anyMatch(f -> fileName.equals(f.getName())) : Boolean.FALSE;
		};
		assertFalse(String.format("File %s already exists", fileName), verifier.call());

		List<FileDto> files = Collections.singletonList(new FileDto(fileName, tempFile.toAbsolutePath().toString(), FileDto.FileType.FILE, 8525172L));
		uploadsRestClient.startUpload(files, accountDto, Encryption.ON, new RequestCallback<Void>() {
			@Override
			public void onFailure(IOException e) {
				fail("Error starting upload.");
			}

			@Override
			public void onSuccess(Void data) throws IOException {

			}
		});

		await().atMost(2, MINUTES).until(verifier, equalTo(true));
		Files.delete(tempFile);
		assertTrue(verifier.call());
	}

}
