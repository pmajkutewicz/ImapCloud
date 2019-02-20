package pl.pamsoft.imapcloud.entity;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskProgressTest {

	private static final long SIZE_1K = 1024L;
	private static final long SIZE_2K = 2048L;
	private static final long SIZE_3K = 4096L;
	private static final String TEST1_PATH = "/tmp/test1";

	@Test
	void shouldInitProperly() {
		//given
		String id = "id";

		//when
		TaskProgress task = new TaskProgress();
		task.setTaskId(id);
		task.setType(TaskType.UPLOAD);
		task.setProgressMap(Collections.emptyMap());
		task.setBytesOverall(SIZE_1K + SIZE_2K + SIZE_3K);

		//then
		assertEquals(id, task.getTaskId());
		assertEquals(SIZE_1K + SIZE_2K + SIZE_3K, task.getBytesOverall());
		assertEquals(0, task.getBytesProcessed());
	}

	@Test
	void shouldUpdateProgress() {
		//given
		Long id = 1333L;
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
		assertEquals(processedBytes, task.getBytesProcessed());
		assertEquals(processedBytes, task.getProgressMap().get(TEST1_PATH).getProgress());
	}

	private Map<String, EntryProgress> createFileList() {
		Map<String, EntryProgress> result = Maps.newHashMap();
		result.put(TEST1_PATH, new EntryProgress(TEST1_PATH, SIZE_1K));
		result.put("/tmp/test2", new EntryProgress("/tmp/test2", SIZE_2K));
		result.put("/tmp/test3", new EntryProgress("/tmp/test3", SIZE_3K));
		return result;
	}
}
