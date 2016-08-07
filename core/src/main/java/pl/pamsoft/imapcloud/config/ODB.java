package pl.pamsoft.imapcloud.config;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class ODB {

	@Autowired
	private OrientGraphFactory graphDB;

	@Autowired
	private OObjectDatabaseTxFactory oObjectDatabaseTxFactory;

	public OrientGraphNoTx getGraphDB() {
		graphDB.getDatabase().activateOnCurrentThread();
		return graphDB.getNoTx();
	}

	@Scope("prototype")
	public OObjectDatabaseTx getObjectDB() {
		OObjectDatabaseTx objectDB = oObjectDatabaseTxFactory.getObjectDB();
		objectDB.activateOnCurrentThread();
		return objectDB;
	}
}
