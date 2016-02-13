package pl.pamsoft.imapcloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.ODB;

@Repository
public abstract class AbstractRepository<T> implements DefaultRepository<T> {

	@Autowired
	private ODB db;

	@Override
	public ODB getDb() {
		return db;
	}
}
