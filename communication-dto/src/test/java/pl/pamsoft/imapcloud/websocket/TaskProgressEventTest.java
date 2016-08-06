package pl.pamsoft.imapcloud.websocket;

import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TaskProgressEventTest {

	private static final long SIZE_1K = 1024L;
	private static final long SIZE_2K = 2048L;
	private static final long SIZE_3K = 4096L;
	private static final String TEST1_PATH = "/tmp/test1";

	@Test
	public void shouldInitProperly() {
		//given
		String id = "id";
		List<FileDto> fileList = createFileList();

		//when
		TaskProgressEvent task = new TaskProgressEvent(TaskType.UPLOAD, id, SIZE_1K + SIZE_2K + SIZE_3K, fileList);

		//then
		assertEquals(task.getTaskId(), id);
		assertEquals(task.getBytesOverall(), SIZE_1K + SIZE_2K + SIZE_3K);
		assertEquals(task.getBytesProcessed(), 0);
	}

	@Test
	public void shouldUpdateProgress() {
		//given
		String id = "task2";
		int processedBytes = 512;
		List<FileDto> fileList = createFileList();

		//when
		TaskProgressEvent task = new TaskProgressEvent(TaskType.UPLOAD, id, SIZE_1K + SIZE_2K + SIZE_3K, fileList);
		task.process(processedBytes, TEST1_PATH, processedBytes);

		//then
		assertEquals(task.getBytesProcessed(), processedBytes);
		assertEquals(task.getFileProgressDataMap().get(TEST1_PATH).getProgress(), processedBytes);
	}

	private List<FileDto> createFileList() {
		return Arrays.asList(
			new FileDto("test1", TEST1_PATH, FileDto.FileType.FILE, SIZE_1K),
			new FileDto("test2", "/tmp/test2", FileDto.FileType.FILE, SIZE_2K),
			new FileDto("test3", "/tmp/test3", FileDto.FileType.FILE, SIZE_3K)
		);
	}
}
