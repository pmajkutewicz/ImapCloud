package pl.pamsoft.imapcloud.entity;

import com.beust.jcommander.internal.Maps;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.Collections;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class TaskProgressTest {

	private static final long SIZE_1K = 1024L;
	private static final long SIZE_2K = 2048L;
	private static final long SIZE_3K = 4096L;
	private static final String TEST1_PATH = "/tmp/test1";

	@Test
	public void shouldInitProperly() {
		//given
		String id = "id";

		//when
		TaskProgress task = new TaskProgress();
		task.setTaskId(id);
		task.setType(TaskType.UPLOAD);
		task.setProgressMap(Collections.emptyMap());
		task.setBytesOverall(SIZE_1K + SIZE_2K + SIZE_3K);

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
		Map<String, EntryProgress> fileList = createFileList();

		//when
		TaskProgress task = new TaskProgress();
		task.setType(TaskType.UPLOAD);
		task.setId(id);
		task.setProgressMap(fileList);
		task.setBytesOverall(SIZE_1K + SIZE_2K + SIZE_3K);
		task.process(TEST1_PATH, processedBytes);

		//then
		assertEquals(task.getBytesProcessed(), processedBytes);
		assertEquals(task.getProgressMap().get(TEST1_PATH).getProgress(), processedBytes);
	}

	private Map<String, EntryProgress> createFileList() {
		Map<String, EntryProgress> result = Maps.newHashMap();
		result.put(TEST1_PATH, new EntryProgress(TEST1_PATH, SIZE_1K));
		result.put("/tmp/test2", new EntryProgress("/tmp/test2", SIZE_2K));
		result.put("/tmp/test3", new EntryProgress("/tmp/test3", SIZE_3K));
		return result;
	}
}
