package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Repository
public class TaskProgressRepository extends AbstractRepository<TaskProgress> {

	public TaskProgress create(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles) {
		OObjectDatabaseTx objectDB = getDb().getObjectDB();
		TaskProgress taskProgressEvent = objectDB.newInstance(TaskProgress.class);
		taskProgressEvent.setType(type);
		taskProgressEvent.setTaskId(taskId);
		taskProgressEvent.setBytesOverall(bytesOverall);
		taskProgressEvent.addSelectedFiles(selectedFiles);
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
	public List<TaskProgress> findAll() {
		OObjectIteratorClass<TaskProgress> events = getDb().getObjectDB().browseClass(TaskProgress.class);
		return IteratorUtils.toList(events.iterator());
	}
}
