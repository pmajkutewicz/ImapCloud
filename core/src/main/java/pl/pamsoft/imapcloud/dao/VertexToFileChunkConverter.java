package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.function.Function;

@Component
@SuppressFBWarnings("SCII_SPOILED_CHILD_INTERFACE_IMPLEMENTOR")
public class VertexToFileChunkConverter extends AbstractVertexConverter implements Function<Vertex, FileChunk> {

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
		Boolean exists = v.getProperty(GraphProperties.FILE_CHUNK_EXISTS);
		if (null != exists) {
			f.setChunkExists(exists);
		}
		Long lastVerifiedAt = v.getProperty(GraphProperties.FILE_CHUNK_LAST_VERIFIED_AT);
		if (null != lastVerifiedAt) {
			f.setLastVerifiedAt(lastVerifiedAt);
		}
		return f;
	}

	private File getOwningFile(Vertex v) {
		Vertex parentVertex = getParentVertex(v, GraphProperties.FILE_CHUNK_EDGE_FILE);
		return parentVertex == null ? null : converter.apply(parentVertex);
	}
}
