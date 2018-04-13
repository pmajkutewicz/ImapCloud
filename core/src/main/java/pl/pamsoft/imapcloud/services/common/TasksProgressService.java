package pl.pamsoft.imapcloud.services.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dao.TaskProgressRepository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.progress.EntryProgressDto;
import pl.pamsoft.imapcloud.dto.progress.TaskProgressDto;
import pl.pamsoft.imapcloud.entity.EntryProgress;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.services.DeletionService;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class TasksProgressService {

	@Autowired
	private TaskProgressRepository taskProgressRepository;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private DeletionService deletionService;

	private Function<EntryProgress, EntryProgressDto> progressToDto = fp -> new EntryProgressDto(fp.getId(), fp.getAbsolutePath(), fp.getSize(), fp.getProgress(), fp.getStatus(), fp.getType());

	private Function<TaskProgress, TaskProgressDto> taskProgressToDto = tp -> {
		Map<String, EntryProgressDto> progressMap = tp.getProgressMap().entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> progressToDto.apply(entry.getValue())));
		return new TaskProgressDto(tp.getId(), tp.getType(), tp.getTaskId(), tp.getBytesOverall(), tp.getBytesProcessed(), progressMap);
	};

	public TaskProgress create(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles) {
		return taskProgressRepository.create(type, taskId, bytesOverall, selectedFiles);
	}

	public TaskProgress create(TaskType type, String taskId, long overall, Map<String, Integer> folderMap) {
		return taskProgressRepository.create(type, taskId, overall, folderMap);
	}

	public TaskProgress persist(TaskProgress event) {
		return taskProgressRepository.save(event);
	}

	public List<TaskProgressDto> findAllTasks() {
		List<TaskProgress> tasks = taskProgressRepository.findAll();
		return tasks.stream().map(taskProgressToDto).collect(toList());
	}

	public void deleteTask(String taskId, boolean deleteUploadedFiles) {
		Optional<TaskProgress> progress = taskProgressRepository.getByTaskId(taskId);
		if (progress.isPresent()) {
			if (deleteUploadedFiles) {
				progress.get().getProgressMap().values().forEach(e -> {
					File fileToDelete = fileRepository.getByAbsolutePath(e.getAbsolutePath());
					deletionService.delete(fileToDelete);
				});
			}
			taskProgressRepository.delete(progress.get());
		}
	}
}
