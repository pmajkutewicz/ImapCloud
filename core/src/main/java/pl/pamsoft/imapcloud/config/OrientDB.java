package pl.pamsoft.imapcloud.config;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.metadata.schema.OSchemaProxyObject;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.pamsoft.imapcloud.entity.Account;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
class OrientDB {

	private static final Logger LOG = LoggerFactory.getLogger(OrientDB.class);

	private OrientGraphFactory graphDB;

	private OObjectDatabaseTx db;

	@Value("${spring.encrypted.orient.url}")
	private String url;

	@Value("${spring.encrypted.orient.username}")
	private String username;

	@Value("${spring.encrypted.orient.password}")
	private String password;

	@PostConstruct
	public void init() {
		LOG.debug("DB Location: {}", url);
		configGraphDb();
		configObjectDb();
	}

	private void configObjectDb() {
		db = OObjectDatabasePool.global().acquire(url, username, password);
		db.setAutomaticSchemaGeneration(true);

		db.getEntityManager().registerEntityClass(Account.class);
		OSchemaProxyObject schema = db.getMetadata().getSchema();
//		if (schema.existsClass(Account.class.getSimpleName())) {
//			OClass accountClass = schema.getClass(Account.class.getSimpleName());
//			if (accountClass.getClassIndex("Account__email") != null) {
//				accountClass.createIndex("Account__email", OClass.INDEX_TYPE.UNIQUE, "email");
//			}
//		}
		schema.save();
	}

	private void configGraphDb() {
		OGlobalConfiguration.FILE_LOCK.setValue(Boolean.FALSE);
		graphDB = new OrientGraphFactory(url, username, password);
		graphDB.setUseLightweightEdges(true);
		OrientGraphNoTx tx = graphDB.getNoTx();
		if (tx.getVertexType(Account.class.getSimpleName()) == null) {
			OrientVertexType vertexType = tx.createVertexType(Account.class.getSimpleName());
			vertexType.createProperty(GraphProperties.ACCOUNT_EMAIL, OType.STRING);
			vertexType.createProperty(GraphProperties.ACCOUNT_LOGIN, OType.STRING);
			vertexType.createProperty(GraphProperties.ACCOUNT_PASSWORD, OType.STRING);
			vertexType.createProperty(GraphProperties.ACCOUNT_IMAP_SERVER, OType.STRING);
			vertexType.createProperty(GraphProperties.ACCOUNT_SIZE_MB, OType.INTEGER);
			vertexType.createProperty(GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB, OType.INTEGER);
			vertexType.createIndex(createIndexName(Account.class, GraphProperties.ACCOUNT_EMAIL),
				OClass.INDEX_TYPE.UNIQUE, GraphProperties.ACCOUNT_EMAIL);
		}
		tx.shutdown();
	}

	private String createIndexName(Class cls, String field) {
		return String.format("%s_%s", cls.getSimpleName(), field);
	}

	@PreDestroy
	public void destroy() {
		graphDB.close();
		db.close();
		LOG.info("DB destroyed");
	}

	@Bean
	public OrientGraphFactory getGraphDB() {
		return graphDB;
	}

	@Bean
	public OObjectDatabaseTx getObjectDB() {
		return db;
	}
}
