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
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.Iterator;

@Repository
public class FileChunkRepository extends AbstractRepository<FileChunk> {

	private static final Logger LOG = LoggerFactory.getLogger(FileChunkRepository.class);

	@Override
	@SuppressFBWarnings("CFS_CONFUSING_FUNCTION_SEMANTICS")
	public FileChunk save(FileChunk chunk) {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
		Iterable<Vertex> storedFiles = graphDB.getVertices("FileChunk.fileChunkUniqueId", chunk.getFileChunkUniqueId());
		Iterator<Vertex> iterator = storedFiles.iterator();
		if (!iterator.hasNext()) {
			OrientVertex orientVertex = graphDB.addVertex(
				"class:" + FileChunk.class.getSimpleName(),
				GraphProperties.FILE_CHUNK_UNIQUE_ID, chunk.getFileChunkUniqueId());
			fillProperties(graphDB, orientVertex, chunk);
			ORecordId id = (ORecordId) orientVertex.getId();
			chunk.setId(id.toString());
			chunk.setVersion(orientVertex.getRecord().getVersion());
			graphDB.shutdown();
		} else {
			LOG.warn("Duplicate chunk with id: {}", chunk.getFileChunkUniqueId());
		}
		return chunk;
	}

	private void fillProperties(OrientGraphNoTx graphDb, OrientVertex fileVertex, FileChunk chunk) {
		OrientVertex vertex = graphDb.getVertex(chunk.getOwnerFile().getId());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_NUMBER, chunk.getChunkNumber());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_HASH, chunk.getChunkHash());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_SIZE, chunk.getSize());
		fileVertex.addEdge(GraphProperties.FILE_CHUNK_EDGE_FILE, vertex);
	}
}
