package pl.pamsoft.imapcloud.dao;

import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TaskProgressRepositoryCustom {

	TaskProgress create(TaskType type, String taskId, long bytesOverall, Map<String, Integer> folderMap);

	TaskProgress create(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles);

	Optional<TaskProgress> getByTaskId(String taskId);
}
