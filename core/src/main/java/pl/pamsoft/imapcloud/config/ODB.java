package pl.pamsoft.imapcloud.config;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ODB {

	@Autowired
	private OrientGraphFactory graphDB;

	@Autowired
	private OObjectDatabaseTx db;

	public OrientGraphNoTx getGraphDB() {
		return graphDB.getNoTx();
	}

	public OObjectDatabaseTx getDb() {
		ODatabaseRecordThreadLocal.INSTANCE.set(db.getUnderlying());
		return db;
	}
}
