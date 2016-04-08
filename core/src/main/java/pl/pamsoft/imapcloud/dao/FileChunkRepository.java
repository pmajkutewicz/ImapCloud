package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class FileChunkRepository extends AbstractRepository<FileChunk> {

	private static final Logger LOG = LoggerFactory.getLogger(FileChunkRepository.class);

	@Autowired
	private Function<Vertex, FileChunk> converter;

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

	public void markChunkVerified(String chunkDbId, boolean isExist) {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
		Vertex storedChunk = graphDB.getVertex(chunkDbId);
		storedChunk.setProperty(GraphProperties.FILE_CHUNK_LAST_VERIFIED_AT, System.currentTimeMillis());
		storedChunk.setProperty(GraphProperties.FILE_CHUNK_EXISTS, isExist);
		graphDB.shutdown();
	}

	public List<FileChunk> getFileChunks(String fileUniqueId) {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
		Iterable<Vertex> vertices = graphDB.getVertices(GraphProperties.FILE_UNIQUE_ID, fileUniqueId);
		Iterator<Vertex> fileIterator = vertices.iterator();
		if (!fileIterator.hasNext()) {
			return Collections.emptyList();
		} else {
			Vertex firstFile = fileIterator.next();
			Spliterator<Edge> spliterator = firstFile.getEdges(Direction.IN, GraphProperties.FILE_CHUNK_EDGE_FILE).spliterator();
			return StreamSupport.stream(spliterator, false)
				.map(r -> r.getVertex(Direction.OUT))
				.map(converter)
				.sorted((o1, o2) -> Integer.compare(o1.getChunkNumber(), o2.getChunkNumber()))
				.collect(Collectors.toList());
		}
	}

	private void fillProperties(OrientGraphNoTx graphDb, OrientVertex fileVertex, FileChunk chunk) {
		OrientVertex vertex = graphDb.getVertex(chunk.getOwnerFile().getId());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_NUMBER, chunk.getChunkNumber());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_HASH, chunk.getChunkHash());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_SIZE, chunk.getSize());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_UNIQUE_ID, chunk.getFileChunkUniqueId());
		fileVertex.setProperty(GraphProperties.FILE_CHUNK_MESSAGE_ID, chunk.getMessageId());
		fileVertex.addEdge(GraphProperties.FILE_CHUNK_EDGE_FILE, vertex);
	}

	@Override
	public Function<Vertex, FileChunk> getConverter() {
		return converter;
	}
}
