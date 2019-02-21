package pl.pamsoft.imapcloud.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.UploadedFileChunkDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.UploadedFileRestClient;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UploadedRestControllerIT extends AbstractIntegrationTest {

	private static final int ONE_MIB = 1024 * 1024;
	private UploadsRestClient uploadsRestClient;
	private AccountRestClient accountRestClient;
	private UploadedFileRestClient uploadedFileRestClient;
	private Common common;

	@Autowired
	private FileRepository fileRepository;

	@BeforeAll
	void init() {
		uploadedFileRestClient = new UploadedFileRestClient(getEndpoint(), getUsername(), getPassword());
		uploadsRestClient = new UploadsRestClient(getEndpoint(), getUsername(), getPassword());
		accountRestClient = new AccountRestClient(getEndpoint(), getUsername(), getPassword());
		common = new Common(accountRestClient, RESPONSE_NOT_RECEIVED, TEST_TIMEOUT);
	}

	@Test
	void shouldReturnUploadedFileData() throws Exception {
		List<UploadedFileDto> results = uploadFileAndReturnData();
		assertEquals(1, results.size());
	}

	@Test
	void shouldReturnEmptyChunkListWhenEmptyFileId() throws Exception {
		List<UploadedFileChunkDto> uploadedChunks = getUploadedChunks("");
		assertTrue(uploadedChunks.isEmpty());
	}

	@Test
	void shouldVerifyUploadedFile() throws Exception {
		List<UploadedFileDto> results = uploadFileAndReturnData();
		assertEquals(1, results.size());
		String fileUniqueId = results.get(0).getFileUniqueId();

		CountDownLatch lock = new CountDownLatch(1);
		uploadedFileRestClient.verifyFile(fileUniqueId, data -> lock.countDown());
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);

		Callable<Boolean> verifier = () -> getUploadedChunks(fileUniqueId).stream().allMatch(UploadedFileChunkDto::getChunkExists);

		await().atMost(2, MINUTES).until(verifier, equalTo(true));
		assertTrue(verifier.call());
	}

	@Test
	void shouldDeleteUploadedFile() throws Exception {
		List<UploadedFileDto> results = uploadFileAndReturnData();
		assertEquals(1, results.size());
		String fileUniqueId = results.get(0).getFileUniqueId();

		CountDownLatch lock = new CountDownLatch(1);
		uploadedFileRestClient.deleteFile(fileUniqueId, data -> lock.countDown());
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);

		Callable<Boolean> verifier = () -> getUploadedFiles().stream().noneMatch(f -> fileUniqueId.equals(f.getFileUniqueId()));

		await().atMost(2, MINUTES).until(verifier, equalTo(true));
		assertTrue(verifier.call());
	}

	private List<UploadedFileDto> uploadFileAndReturnData() throws Exception {
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

	private List<UploadedFileChunkDto> getUploadedChunks(String fileId) throws InterruptedException {
		List<UploadedFileChunkDto> results = new ArrayList<>();
		CountDownLatch lock = new CountDownLatch(1);
		uploadedFileRestClient.getUploadedFileChunks(fileId, data -> {
			results.addAll(data.getFileChunks());
			lock.countDown();
		});
		assertTrue(lock.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS), RESPONSE_NOT_RECEIVED);
		return results;
	}

}
