package pl.pamsoft.imapcloud.services.upload;


import org.mockito.Mockito;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import java.security.MessageDigest;

import static org.testng.Assert.assertEquals;

public class UploadChunkHasherTest {

	private Statistics statistics = Mockito.mock(Statistics.class);
	private PerformanceDataService performanceDataService = Mockito.mock(PerformanceDataService.class);

	@Test
	public void shouldCalculateSha512Hash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);

		//when
		UploadChunkHasher uploadChunkHasher = new UploadChunkHasher(MessageDigest.getInstance("SHA-512"), statistics, performanceDataService);
		UploadChunkContainer uploadChunkContainer = UploadChunkContainer.addChunk(new UploadChunkContainer("testId", mockedFileDto), test.getBytes().length, 0, test.getBytes(), 1, false);
		UploadChunkContainer result = uploadChunkHasher.apply(uploadChunkContainer);

		//then
		assertEquals(result.getChunkHash(), "911373ca94482a9f0ef1c23a0c0abbd166f1540e6da544bb920dba59377051874b8d8c93014c51c764bd754185cb9ca3fcf9adfbcfcb84b0df8aa856dd5d4209");
	}

	@Test
	public void shouldCalculateMD5Hash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = Mockito.mock(FileDto.class);

		//when
		UploadChunkHasher uploadChunkHasher = new UploadChunkHasher(MessageDigest.getInstance("MD5"), statistics, performanceDataService);
		UploadChunkContainer uploadChunkContainer = UploadChunkContainer.addChunk(new UploadChunkContainer("testId", mockedFileDto), test.getBytes().length, 0, test.getBytes(), 1, false);
		UploadChunkContainer result = uploadChunkHasher.apply(uploadChunkContainer);

		//then
		assertEquals(result.getChunkHash(), "3a760fae784d30a1b50e304e97a17355");
	}
}
