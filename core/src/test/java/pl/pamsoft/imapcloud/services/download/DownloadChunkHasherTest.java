package pl.pamsoft.imapcloud.services.download;


import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DownloadChunkHasherTest {

	private MonitoringHelper monitoringHelper = mock(MonitoringHelper.class);

	@Test
	void shouldCalculateHash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = TestUtils.mockFileDto();
		FileChunk fc = TestUtils.createFileChunk("irrelevant", false);

		//when
		DownloadChunkHasher downloadChunkHasher = new DownloadChunkHasher(monitoringHelper);
		DownloadChunkContainer dcc = new DownloadChunkContainer("id", fc, mockedFileDto, fc.getChunkHash(), fc.getOwnerFile().getFileHash());
		DownloadChunkContainer downloadChunkContainer = DownloadChunkContainer.addData(dcc, test.getBytes());
		DownloadChunkContainer result = downloadChunkHasher.apply(downloadChunkContainer);

		//then
		assertEquals("9dd1b3f3b26ecb78", result.getChunkHash());
	}
}
