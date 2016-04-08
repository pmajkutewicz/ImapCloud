package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;

import java.util.function.Function;

@Component
@SuppressFBWarnings("SCII_SPOILED_CHILD_INTERFACE_IMPLEMENTOR")
public class VertexToFileConverter extends AbstractVertexConverter implements Function<Vertex, File> {

	@Autowired
	private AccountRepository accountRepository;

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
		f.setCompleted(v.getProperty(GraphProperties.FILE_COMPLETED));
		f.setOwnerAccount(getOwningAccount(v));
		return f;
	}

	private Account getOwningAccount(Vertex v) {
		Vertex parentVertex = getParentVertex(v, GraphProperties.FILE_EDGE_ACCOUNT);
		if (null != parentVertex) {
			return accountRepository.getById(parentVertex.getId().toString());
		} else {
			return null;
		}
	}
}
