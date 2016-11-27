package pl.pamsoft.imapcloud.services.download;


import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

public class DownloadChunkHasherTest {

	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@Test
	public void shouldCalculateHash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = TestUtils.mockFileDto();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", false);

		//when
		DownloadChunkHasher downloadChunkHasher = new DownloadChunkHasher(monitoringHelper);
		DownloadChunkContainer dcc = new DownloadChunkContainer("id", fc, mockedFileDto);
		DownloadChunkContainer downloadChunkContainer = DownloadChunkContainer.addData(dcc, test.getBytes());
		DownloadChunkContainer result = downloadChunkHasher.apply(downloadChunkContainer);

		//then
		assertEquals(result.getChunkHash(), "9dd1b3f3b26ecb78");
	}
}
