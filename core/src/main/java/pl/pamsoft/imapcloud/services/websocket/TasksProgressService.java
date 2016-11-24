package pl.pamsoft.imapcloud.services.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.TaskProgressRepository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.progress.FileProgressDto;
import pl.pamsoft.imapcloud.dto.progress.TaskProgressDto;
import pl.pamsoft.imapcloud.entity.FileProgress;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.FileProgressData;
import pl.pamsoft.imapcloud.websocket.TaskProgressEvent;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class TasksProgressService extends AbstractService<TaskProgress> {

	private static final Logger LOG = LoggerFactory.getLogger(TasksProgressService.class);

	@Autowired
	private TasksProgressEventService tasksProgressEventService;

	@Autowired
	private TaskProgressRepository taskProgressRepository;

	private Function<FileProgress, FileProgressData> convertFp = fp ->
		new FileProgressData(fp.getId(), fp.getAbsolutePath(), fp.getSize(), fp.getProgress());

	private Function<TaskProgress, TaskProgressEvent> convert = tp -> {
		Map<String, FileProgressData> result = new HashMap<>();
		tp.getFileProgressDataMap().entrySet().forEach(entry ->
			result.put(entry.getKey(), convertFp.apply(entry.getValue()))
		);
		return new TaskProgressEvent(tp.getId(), tp.getType(), tp.getTaskId(), tp.getBytesOverall(),
			tp.getBytesProcessed(), result);
	};

	private Function<FileProgress, FileProgressDto> fileProgressToDto = fp -> new FileProgressDto(fp.getId(), fp.getAbsolutePath(), fp.getSize(), fp.getProgress());

	private Function<TaskProgress, TaskProgressDto> taskProgressToDto = tp -> {
		Map<String, FileProgressDto> fileProgressDataMap = tp.getFileProgressDataMap().entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> fileProgressToDto.apply(entry.getValue())));
		return new TaskProgressDto(tp.getId(), tp.getType(), tp.getTaskId(), tp.getBytesOverall(), tp.getBytesProcessed(), fileProgressDataMap);
	};

	public TaskProgress create(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles) {
		return taskProgressRepository.create(type, taskId, bytesOverall, selectedFiles);
	}

	public void update(TaskProgress event) {
		try {
			taskProgressRepository.save(event);
		} catch (IOException e) {
			LOG.error("Error storing progress event", e);
		}
	}

	@Override
	public void broadcast(TaskProgress event) {
		tasksProgressEventService.broadcast(convert.apply(event));
	}

	public List<TaskProgressDto> findAllTasks() {
		List<TaskProgress> tasks = taskProgressRepository.findAll();
		return tasks.stream().map(taskProgressToDto).collect(toList());
	}
}
