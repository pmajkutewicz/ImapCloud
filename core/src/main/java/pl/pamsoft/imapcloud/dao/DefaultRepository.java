package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.ODB;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@Repository
public interface DefaultRepository<T> {

	default T getById(String id) {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
		Vertex storedFile = graphDB.getVertex(new ORecordId(id));
		return getConverter().apply(storedFile);
	}

	@SuppressWarnings("unchecked")
	default Collection<T> findAll() {
		java.lang.Class<T> tClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), DefaultRepository.class);
		Iterable<Vertex> verticesOfClass = getDb().getGraphDB().getVerticesOfClass(tClass.getSimpleName());

		List<T> result = new LinkedList<>();
		for (Vertex v : verticesOfClass) {
			T pojo = getConverter().apply(v);
			result.add(pojo);
		}

		return result;
	}

	T save(T entity) throws IOException;

	Function<Vertex, T> getConverter();

	ODB getDb();
}
