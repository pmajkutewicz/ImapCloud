package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.File;

import java.util.Iterator;
import java.util.function.Function;

@Repository
public class FileRepository extends AbstractRepository<File> {

	private static final Logger LOG = LoggerFactory.getLogger(FileRepository.class);

	private Function<Vertex, File> converter = v -> {
		File f = new File();
		f.setId(v.getId().toString());
		f.setVersion(((OrientVertex) v).getRecord().getVersion());
		f.setSize(v.getProperty(GraphProperties.FILE_SIZE));
		f.setName(v.getProperty(GraphProperties.FILE_NAME));
		f.setAbsolutePath(v.getProperty(GraphProperties.FILE_ABSOLUTE_PATH));
		f.setFileHash(v.getProperty(GraphProperties.FILE_HASH));
		//TODO: add owner account
		return f;
	};

	@Override
	public File getById(String id) {
		OrientGraph graphDB = getDb().getGraphDB();
		Vertex storedFile = graphDB.getVertex(new ORecordId(id));
		return converter.apply(storedFile);
	}

	@Override
	@SuppressFBWarnings("CFS_CONFUSING_FUNCTION_SEMANTICS")
	public File save(File file) {
		OrientGraph graphDB = getDb().getGraphDB();
		graphDB.begin();
		Iterable<Vertex> storedFiles = graphDB.getVertices(File.class.getSimpleName(),
			new String[]{GraphProperties.FILE_ABSOLUTE_PATH, GraphProperties.FILE_HASH}, new Object[]{file.getAbsolutePath(), file.getFileHash()});
		Iterator<Vertex> iterator = storedFiles.iterator();
		if (!iterator.hasNext()) {
			OrientVertex orientVertex = graphDB.addVertex(
				"class:" + File.class.getSimpleName(),
				GraphProperties.FILE_ABSOLUTE_PATH, file.getAbsolutePath(),
				GraphProperties.FILE_HASH, file.getFileHash());
			fillProperties(graphDB, orientVertex, file);
			ORecordId id = (ORecordId) orientVertex.getId();
			graphDB.commit();
			file.setId(id.toString());
			file.setVersion(orientVertex.getRecord().getVersion());
			graphDB.shutdown();
		} else {
			LOG.warn("Duplicate file: {}", file.getAbsolutePath());
		}
		return file;
	}

	private void fillProperties(OrientGraph graphDb, OrientVertex fileVertex, File file) {
		OrientVertex vertex = graphDb.getVertex(file.getOwnerAccount().getId());
		fileVertex.setProperty(GraphProperties.FILE_NAME, file.getName());
		fileVertex.setProperty(GraphProperties.FILE_ABSOLUTE_PATH, file.getAbsolutePath());
		fileVertex.setProperty(GraphProperties.FILE_SIZE, file.getSize());
		fileVertex.addEdge(GraphProperties.FILE_EDGE_ACCOUNT, vertex);
	}
}
