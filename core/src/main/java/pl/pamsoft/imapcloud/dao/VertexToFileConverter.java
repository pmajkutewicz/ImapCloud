package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.File;

import java.util.function.Function;

@Component
public class VertexToFileConverter implements Function<Vertex, File> {
	@Override
	public File apply(Vertex v) {
		File f = new File();
		f.setId(v.getId().toString());
		f.setVersion(((OrientVertex) v).getRecord().getVersion());
		f.setSize(v.getProperty(GraphProperties.FILE_SIZE));
		f.setFileUniqueId(v.getProperty(GraphProperties.FILE_UNIQUE_ID));
		f.setName(v.getProperty(GraphProperties.FILE_NAME));
		f.setAbsolutePath(v.getProperty(GraphProperties.FILE_ABSOLUTE_PATH));
		f.setFileHash(v.getProperty(GraphProperties.FILE_HASH));
		//TODO: add owner account
		return f;
	}
}
