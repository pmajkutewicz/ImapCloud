package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.FileProgress;

import java.util.List;
import java.util.function.Function;

@Repository
public class FileProgressRepository extends AbstractRepository<FileProgress> {

	@Override
	public FileProgress save(FileProgress fileProgress) {
		OObjectDatabaseTx objectDB = getDb().getObjectDB();
		objectDB.save(fileProgress);
		objectDB.commit(true);
		objectDB.close();
		return fileProgress;
	}

	@Override
	public Function<Vertex, FileProgress> getConverter() {
		throw new RuntimeException("No way!");
	}

	@Override
	public FileProgress getById(String id) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FileProgress> findAll() {
		OObjectIteratorClass<FileProgress> events = getDb().getObjectDB().browseClass(FileProgress.class);
		return IteratorUtils.toList(events.iterator());
	}
}
