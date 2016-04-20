package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.config.ODB;

import java.util.Iterator;

@Repository
public abstract class AbstractRepository<T> implements DefaultRepository<T> {

	@Autowired
	private ODB db;

	@Override
	public ODB getDb() {
		return db;
	}

	Iterator<Vertex> findByFileUniqueId(String fileUniqueId) {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
		Iterable<Vertex> storedFiles = graphDB.getVertices(GraphProperties.FILE_UNIQUE_ID, fileUniqueId);
		return storedFiles.iterator();
	}
}
