package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.Iterator;
import java.util.function.Function;

@Component
public class VertexToFileChunkConverter implements Function<Vertex, FileChunk> {

	@Autowired
	private Function<Vertex, File> converter;

	@Override
	public FileChunk apply(Vertex v) {
		FileChunk f = new FileChunk();
		f.setId(v.getId().toString());
		f.setVersion(((OrientVertex) v).getRecord().getVersion());
		f.setChunkHash(v.getProperty(GraphProperties.FILE_CHUNK_HASH));
		f.setMessageId(v.getProperty(GraphProperties.FILE_CHUNK_MESSAGE_ID));
		f.setChunkNumber(v.getProperty(GraphProperties.FILE_CHUNK_NUMBER));
		f.setSize(v.getProperty(GraphProperties.FILE_CHUNK_SIZE));
		f.setFileChunkUniqueId(v.getProperty(GraphProperties.FILE_CHUNK_UNIQUE_ID));
		f.setOwnerFile(getOwningFile(v));
		return f;
	}

	private File getOwningFile(Vertex v) {
		Iterator<Edge> iterator = v.getEdges(Direction.OUT, GraphProperties.FILE_CHUNK_EDGE_FILE).iterator();
		if (iterator.hasNext()) {
			Vertex vertex = iterator.next().getVertex(Direction.IN);
			return converter.apply(vertex);
		}
		return null;
	}
}
