package pl.pamsoft.imapcloud.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UploadedRestControllerIT extends AbstractIntegrationTest {

	private static final int ONE_MIB = 1024 * 1024;
	private UploadsRestClient uploadsRestClient;
	private AccountRestClient accountRestClient;
	private UploadedFileRestClient uploadedFileRestClient;
	private Common common;

	@Autowired
	private FileRepository fileRepository;

	@BeforeClass
	public void init() {
		uploadedFileRestClient = new UploadedFileRestClient(getEndpoint(), "user", getPassword());
		uploadsRestClient = new UploadsRestClient(getEndpoint(), "user", getPassword());
		accountRestClient = new AccountRestClient(getEndpoint(), "user", getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
	}

	@Test
	public void shouldReturnUploadedFileData() throws Exception {
		List<UploadedFileDto> results = shouldUploadFileAndReturnData();
		assertEquals(results.size(), 1);
	}

	private List<UploadedFileDto> shouldUploadFileAndReturnData() throws Exception {
		Path uploadedFile = common.shouldUploadFile(uploadsRestClient, fileRepository, ONE_MIB);
		Files.delete(uploadedFile);

		List<UploadedFileDto> results = getUploadedFiles();
		return results.stream().filter(f -> uploadedFile.getFileName().toString().equals(f.getName())).collect(Collectors.toList());
	}

	private List<UploadedFileDto> getUploadedFiles() throws InterruptedException {
		List<UploadedFileDto> results = new ArrayList<>();
		CountDownLatch lock = new CountDownLatch(1);
		uploadedFileRestClient.getUploadedFiles(data -> {
			results.addAll(data.getFiles());
			lock.countDown();
		});
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
		return results;
	}

}
