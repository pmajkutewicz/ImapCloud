package pl.pamsoft.imapcloud.config;

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ODB {

	@Autowired
	private OrientGraphFactory graphDB;

	public OrientGraphNoTx getGraphDB() {
		graphDB.getDatabase().activateOnCurrentThread();
		return graphDB.getNoTx();
	}

}
