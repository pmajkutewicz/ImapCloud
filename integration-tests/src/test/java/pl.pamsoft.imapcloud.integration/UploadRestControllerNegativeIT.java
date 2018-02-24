package pl.pamsoft.imapcloud.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.integration.greenmail.AllowOnlyFirstRequestAppendCommand;
import pl.pamsoft.imapcloud.requests.Encryption;
import pl.pamsoft.imapcloud.rest.RequestCallback;
import pl.pamsoft.imapcloud.rest.UploadsRestClient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.testng.Assert.assertFalse;

public class UploadRestControllerNegativeIT extends AbstractGreenMailIntegrationTest {

	private static final int ONE_MIB = 1024 * 1024 * 10;
	private UploadsRestClient uploadsRestClient;
	@Autowired
	private FileRepository fileRepository;

	@BeforeClass
	public void init() throws IOException, InterruptedException {
		super.init();
		uploadsRestClient = new UploadsRestClient(getEndpoint(), getUsername(), getPassword());
	}

	@Test
	public void shouldUploadFile() throws Exception {
		sendFile(testAccountDto);
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

	@Override
	Class getAppendClass() {
		return AllowOnlyFirstRequestAppendCommand.class;
	}

}
