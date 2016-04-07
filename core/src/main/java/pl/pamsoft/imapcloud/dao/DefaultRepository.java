package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.ODB;

import java.io.IOException;
import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

		int characteristics = Spliterator.SORTED | Spliterator.ORDERED;
		Spliterator<Vertex> spliterator = Spliterators.spliteratorUnknownSize(verticesOfClass.iterator(), characteristics);
		boolean parallel = false;
		Stream<Vertex> stream = StreamSupport.stream(spliterator, parallel);

		return stream.map(getConverter()).collect(Collectors.toList());
	}

	T save(T entity) throws IOException;

	Function<Vertex, T> getConverter();

	ODB getDb();
}
