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
		f.setFileUniqueId(v.getProperty(GraphProperties.FILE_UNIQUE_ID));
		f.setName(v.getProperty(GraphProperties.FILE_NAME));
		f.setAbsolutePath(v.getProperty(GraphProperties.FILE_ABSOLUTE_PATH));

		return f;
	};

	public File getFileByUniqueId(String id) {
		OrientGraph graphDB = getDb().getGraphDB();
		Iterable<Vertex> storedFiles = graphDB.getVertices("File.fileUniqueId", id);
		return converter.apply(storedFiles.iterator().next());
	}

	@Override
	@SuppressFBWarnings("CFS_CONFUSING_FUNCTION_SEMANTICS")
	public File save(File file) {
		OrientGraph graphDB = getDb().getGraphDB();
		graphDB.begin();
		Iterable<Vertex> storedFiles = graphDB.getVertices("File.fileUniqueId", file.getFileUniqueId());
		Iterator<Vertex> iterator = storedFiles.iterator();
		if (!iterator.hasNext()) {
			OrientVertex orientVertex = graphDB.addVertex(
				"class:" + File.class.getSimpleName(),
				GraphProperties.FILE_UNIQUE_ID, file.getFileUniqueId());
			fillProperties(graphDB, orientVertex, file);
			ORecordId id = (ORecordId) orientVertex.getId();
			file.setId(id.toString());
			file.setVersion(orientVertex.getRecord().getVersion());
			graphDB.commit();
			graphDB.shutdown();
		} else {
			LOG.warn("Duplicate file with id: {}", file.getFileUniqueId());
		}
		return file;
	}

	private void fillProperties(OrientGraph graphDb, OrientVertex fileVertex, File file) {
		OrientVertex vertex = graphDb.getVertex(file.getOwnerAccount().getId());
		fileVertex.setProperty(GraphProperties.FILE_NAME, file.getName());
		fileVertex.setProperty(GraphProperties.FILE_ABSOLUTE_PATH, file.getAbsolutePath());
		fileVertex.setProperty(GraphProperties.FILE_SIZE, file.getSize());
		fileVertex.setProperty(GraphProperties.FILE_UNIQUE_ID, file.getFileUniqueId());
		fileVertex.addEdge(GraphProperties.FILE_EDGE_ACCOUNT, vertex);
	}
}
