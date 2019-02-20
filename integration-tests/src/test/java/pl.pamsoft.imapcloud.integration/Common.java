package pl.pamsoft.imapcloud.integration;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.requests.Encryption;
import pl.pamsoft.imapcloud.rest.AccountRestClient;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class Common {

	private final String responseNotReceivedMsg;
	private final int testTimeout;
	private final AccountRestClient accountRestClient;

	public Common(AccountRestClient accountRestClient, String responseNotReceivedMsg, int testTimeout) {
		this.responseNotReceivedMsg = responseNotReceivedMsg;
		this.testTimeout = testTimeout;
		this.accountRestClient = accountRestClient;
	}

	public AccountDto shouldCreateAccount(String username, String password, String cryptoKey, String expectedAccountEmail) throws IOException, InterruptedException {
		return shouldCreateAccount(username, password, cryptoKey, expectedAccountEmail, "vfs", a-> "tmp".equals(a.getProperty("fs")));
	}
	public AccountDto shouldCreateAccount(String username, String password, String cryptoKey, String expectedAccountEmail, String type) throws IOException, InterruptedException {
		return shouldCreateAccount(username, password, cryptoKey, expectedAccountEmail, type, a -> true);
	}
	public AccountDto shouldCreateAccount(String username, String password, String cryptoKey, String expectedAccountEmail, String type, Predicate<AccountInfo> filter) throws IOException, InterruptedException {
		// get available accounts
		CountDownLatch lock = new CountDownLatch(1);
		List<AccountInfo> responses = new ArrayList<>();
		accountRestClient.getAvailableAccounts(accountProviders -> {
				responses.addAll(accountProviders.getAccountProviders());
				lock.countDown();
			}
		);
		assertTrue(lock.await(testTimeout, TimeUnit.MILLISECONDS), responseNotReceivedMsg);

		Optional<AccountInfo> accountInfo = responses.stream().filter(a -> type.equals(a.getType())).filter(filter).findFirst();
		if (!accountInfo.isPresent()) {
			fail("No VFS account available");
		}

		//create account
		CountDownLatch lock2 = new CountDownLatch(1);
		accountRestClient.createAccount(accountInfo.get(), username, password, cryptoKey, callback -> lock2.countDown());
		assertTrue(lock2.await(testTimeout, TimeUnit.MILLISECONDS), responseNotReceivedMsg);

		//verify created Account
		List<AccountDto> result = new ArrayList<>();
		CountDownLatch lock3 = new CountDownLatch(1);
		accountRestClient.listAccounts(response -> {
			Optional<AccountDto> createdAccount = response.getAccount().stream().filter(a -> expectedAccountEmail.equals(a.getEmail())).findFirst();
			assertTrue(createdAccount.isPresent());
			result.add(createdAccount.get());
			lock3.countDown();
		});
		assertTrue(lock3.await(testTimeout, TimeUnit.MILLISECONDS), responseNotReceivedMsg);

		return result.get(0);
	}

	@Test
	Path shouldUploadFile(UploadsRestClient uploadsRestClient, FileRepository fileRepository, long fileSize) throws Exception {
		String username = RandomStringUtils.randomAlphabetic(10);
		AccountDto accountDto = shouldCreateAccount(username, "test", "key", String.format("%s@localhost_tmp", username));
		Path tempFile = TestUtils.createTempFile(fileSize);
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
				fail("Error starting upload.");
			}

			@Override
			public void onSuccess(Void data) throws IOException {

			}
		});

		await().atMost(2, MINUTES).until(verifier, equalTo(true));
		assertTrue(verifier.call());
		return tempFile;
	}
}
