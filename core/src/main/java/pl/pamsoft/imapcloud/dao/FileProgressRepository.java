package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.EntryProgress;

import java.util.List;
import java.util.function.Function;

@Repository
public class FileProgressRepository extends AbstractRepository<EntryProgress> {

	@Override
	public EntryProgress save(EntryProgress entryProgress) {
		OObjectDatabaseTx objectDB = getDb().getObjectDB();
		objectDB.save(entryProgress);
		objectDB.commit(true);
		objectDB.close();
		return entryProgress;
	}

	@Override
	public Function<Vertex, EntryProgress> getConverter() {
		throw new RuntimeException("No way!");
	}

	@Override
	public EntryProgress getById(String id) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EntryProgress> findAll() {
		OObjectIteratorClass<EntryProgress> events = getDb().getObjectDB().browseClass(EntryProgress.class);
		return IteratorUtils.toList(events.iterator());
	}
}
