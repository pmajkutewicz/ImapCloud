package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import com.tinkerpop.blueprints.Vertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileProgress;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class TaskProgressRepository extends AbstractRepository<TaskProgress> {

	@Autowired
	private FileProgressRepository fileProgressRepository;

	public TaskProgress create(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles) {
		OObjectDatabaseTx objectDB = getDb().getObjectDB();
		TaskProgress taskProgressEvent = objectDB.newInstance(TaskProgress.class);
		taskProgressEvent.setType(type);
		taskProgressEvent.setTaskId(taskId);
		taskProgressEvent.setBytesOverall(bytesOverall);
		Map<String, FileProgress> fileProgressMap = selectedFiles.stream()
			.map(file -> new FileProgress(file.getAbsolutePath(), file.getSize()))
			.peek(fileProgressRepository::save)
			.collect(Collectors.toMap(FileProgress::getAbsolutePath, c -> c));
		taskProgressEvent.setFileProgressDataMap(fileProgressMap);
		objectDB.save(taskProgressEvent);
		objectDB.commit(true);
		objectDB.close();
		return taskProgressEvent;
	}

	@Override
	public TaskProgress save(TaskProgress entity) throws IOException {
		OObjectDatabaseTx objectDB = getDb().getObjectDB();
		TaskProgress result = objectDB.save(entity);
		objectDB.commit(true);
		objectDB.close();
		return result;
	}

	@Override
	public Function<Vertex, TaskProgress> getConverter() {
		throw new RuntimeException("No way!");
	}

	@Override
	public TaskProgress getById(String id) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	@SuppressFBWarnings("SGSU_SUSPICIOUS_GETTER_SETTER_USE")
	public List<TaskProgress> findAll() {
		OObjectIteratorClass<TaskProgress> events = getDb().getObjectDB().browseClass(TaskProgress.class).setFetchPlan("*:-1");
		ArrayList<TaskProgress> result = new ArrayList<>();
		for (TaskProgress event : events) {
			//FIXME: ugly hack to load lazy values
			event.setFileProgressDataMap(event.getFileProgressDataMap());
			result.add(event);
		}
		return result;
	}
}
