package pl.pamsoft.imapcloud.services.upload;


import org.mockito.Mockito;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.TestUtils;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import static org.testng.Assert.assertEquals;

public class UploadChunkHasherTest {

	private MonitoringHelper monitoringHelper= Mockito.mock(MonitoringHelper.class);

	@Test
	public void shouldCalculateHash() throws Exception {
		//given
		String test = "testData";
		FileDto mockedFileDto = TestUtils.mockFileDto();

		//when
		UploadChunkHasher uploadChunkHasher = new UploadChunkHasher(monitoringHelper);
		UploadChunkContainer uploadChunkContainer = UploadChunkContainer.addChunk(new UploadChunkContainer("testId", mockedFileDto), test.getBytes().length, 0, test.getBytes(), 1, false);
		UploadChunkContainer result = uploadChunkHasher.apply(uploadChunkContainer);

		//then
		assertEquals(result.getChunkHash(), "9dd1b3f3b26ecb78");
	}
}
