package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.File;

import java.util.Iterator;

@Repository
public class FileRepository extends AbstractRepository<File> {

	private static final Logger LOG = LoggerFactory.getLogger(FileRepository.class);

	@Override
	@SuppressFBWarnings("CFS_CONFUSING_FUNCTION_SEMANTICS")
	public File save(File file) {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
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
			graphDB.shutdown();
		} else {
			LOG.warn("Duplicate file with id: {}", file.getFileUniqueId());
		}
		return file;
	}

	private void fillProperties(OrientGraphNoTx graphDb, OrientVertex fileVertex, File file) {
		OrientVertex vertex = graphDb.getVertex(file.getOwnerAccount().getId());
		fileVertex.setProperty(GraphProperties.FILE_NAME, file.getName());
		fileVertex.setProperty(GraphProperties.FILE_ABSOLUTE_PATH, file.getAbsolutePath());
		fileVertex.setProperty(GraphProperties.FILE_SIZE, file.getSize());
		fileVertex.addEdge(GraphProperties.FILE_EDGE_ACCOUNT, vertex);
	}
}
