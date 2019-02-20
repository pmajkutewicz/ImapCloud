package pl.pamsoft.imapcloud.services.upload;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UploadChunkHasherTest {

	private MonitoringHelper monitoringHelper= Mockito.mock(MonitoringHelper.class);

	@Test
	void shouldCalculateHash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = TestUtils.mockFileDto();

		//when
		UploadChunkHasher uploadChunkHasher = new UploadChunkHasher(monitoringHelper);
		UploadChunkContainer uploadChunkContainer = UploadChunkContainer.addChunk(new UploadChunkContainer("testId", mockedFileDto), test.getBytes().length, 0, test.getBytes(), 1, false);
		UploadChunkContainer result = uploadChunkHasher.apply(uploadChunkContainer);

		//then
		assertEquals("9dd1b3f3b26ecb78", result.getChunkHash());
	}
}
