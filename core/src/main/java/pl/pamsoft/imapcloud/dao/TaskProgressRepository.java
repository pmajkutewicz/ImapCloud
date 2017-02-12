package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import com.tinkerpop.blueprints.Vertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.FileProgress;
import pl.pamsoft.imapcloud.entity.TaskProgress;
import pl.pamsoft.imapcloud.websocket.TaskType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Repository
public class TaskProgressRepository extends AbstractRepository<TaskProgress> {

	public TaskProgress create(TaskType type, String taskId, long bytesOverall, List<FileDto> selectedFiles) {
		OObjectDatabaseTx objectDB = getDb().getObjectDB();
		TaskProgress taskProgressEvent = objectDB.newInstance(TaskProgress.class);
		taskProgressEvent.setType(type);
		taskProgressEvent.setTaskId(taskId);
		taskProgressEvent.setBytesOverall(bytesOverall);
		Map<String, FileProgress> result = new ConcurrentHashMap<>();
		for (FileDto file : selectedFiles) {
			FileProgress fp = objectDB.newInstance(FileProgress.class, file.getAbsolutePath(), file.getSize());
			result.put(fp.getAbsolutePath(), fp);
		}
		taskProgressEvent.setFileProgressDataMap(result);
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
		OObjectDatabaseTx objectDB = getDb().getObjectDB();
		OObjectIteratorClass<TaskProgress> events = objectDB.browseClass(TaskProgress.class).setFetchPlan("*:-1");
		ArrayList<TaskProgress> result = new ArrayList<>();
		for (TaskProgress event : events) {
			objectDB.reload(event);
			objectDB.detach(event);
			for (FileProgress fp : event.getFileProgressDataMap().values()) {
				objectDB.reload(fp);
				objectDB.detach(fp);
			}
			result.add(event);
		}
		return result;
	}
}
