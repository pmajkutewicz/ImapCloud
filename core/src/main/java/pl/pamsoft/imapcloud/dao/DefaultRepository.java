package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.ODB;

import java.util.Collection;
import java.util.LinkedList;

@Repository
public interface DefaultRepository<T> {

	default T save(T entity) {
		return getDb().getDb().save(entity);
	}

	default T getById(String fileId) {
		return getDb().getDb().load(new ORecordId(fileId));
	}

	@SuppressWarnings("unchecked")
	default Collection<T> findAll() {
		java.lang.Class<T> tClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), DefaultRepository.class);
		OObjectIteratorClass<T> accounts = getDb().getDb().browseClass(tClass);
		Collection<T> result = new LinkedList<>();
		accounts.forEachRemaining(result::add);
		return result;
	}

	ODB getDb();
}
