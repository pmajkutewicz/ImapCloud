package pl.pamsoft.imapcloud.services.common;

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
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class TasksProgressService {

	private static final Logger LOG = LoggerFactory.getLogger(TasksProgressService.class);

	@Autowired
	private TaskProgressRepository taskProgressRepository;

	private Function<FileProgress, FileProgressDto> fileProgressToDto = fp -> new FileProgressDto(fp.getId(), fp.getAbsolutePath(), fp.getSize(), fp.getProgress(), fp.getStatus());

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

	public List<TaskProgressDto> findAllTasks() {
		List<TaskProgress> tasks = taskProgressRepository.findAll();
		return tasks.stream().map(taskProgressToDto).collect(toList());
	}
}
