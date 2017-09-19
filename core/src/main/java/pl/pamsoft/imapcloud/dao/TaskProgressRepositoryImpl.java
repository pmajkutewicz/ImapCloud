package pl.pamsoft.imapcloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.EntryProgress;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TaskProgressRepositoryImpl implements TaskProgressRepositoryCustom {

	@Lazy
	@Autowired
	private TaskProgressRepository taskProgressRepository;

	@Override
	public TaskProgress create(TaskType type, String taskId, long bytesOverall, Map<String, Integer> folderMap) {
		Map<String, EntryProgress> files = folderMap.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, v -> new EntryProgress(v.getKey(), v.getValue())));
		return createInt(type, taskId, bytesOverall, files);
	}

	@Override
	public TaskProgress create(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles) {
		Map<String, EntryProgress> files = selectedFiles.stream()
			.collect(Collectors.toMap(FileDto::getAbsolutePath, v -> new EntryProgress(v.getAbsolutePath(), v.getSize())));
		return createInt(type, taskId, bytesOverall, files);
	}

	private TaskProgress createInt(TaskType type, String taskId, long bytesOverall, Map<String, EntryProgress> entries) {
		TaskProgress taskProgressEvent = new TaskProgress();
		taskProgressEvent.setType(type);
		taskProgressEvent.setTaskId(taskId);
		taskProgressEvent.setBytesOverall(bytesOverall);
		taskProgressEvent.setProgressMap(entries);
		taskProgressRepository.save(taskProgressEvent);
		return taskProgressEvent;
	}

}
