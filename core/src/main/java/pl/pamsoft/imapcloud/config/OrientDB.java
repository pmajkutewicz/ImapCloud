package pl.pamsoft.imapcloud.config;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.metadata.schema.OSchemaProxyObject;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB;
import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_CRYPTO_KEY;
import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_EMAIL;
import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_IMAP_SERVER;
import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_LOGIN;
import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_MAX_CONCURRENT_CONNECTIONS;
import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_PASSWORD;
import static pl.pamsoft.imapcloud.config.GraphProperties.ACCOUNT_SIZE_MB;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_ABSOLUTE_PATH;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_CHUNK_HASH;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_CHUNK_NUMBER;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_CHUNK_SIZE;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_CHUNK_UNIQUE_ID;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_HASH;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_NAME;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_SIZE;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_UNIQUE_ID;

@Configuration
class OrientDB {

	private static final Logger LOG = LoggerFactory.getLogger(OrientDB.class);

	private OrientGraphFactory graphDB;

	private OObjectDatabaseTx db;

	@Value("${spring.data.orient.url}")
	private String url;

	@Value("${spring.data.orient.username}")
	private String username;

	@Value("${spring.data.orient.password}")
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
			vertexType.createProperty(ACCOUNT_EMAIL, OType.STRING);
			vertexType.createProperty(ACCOUNT_LOGIN, OType.STRING);
			vertexType.createProperty(ACCOUNT_PASSWORD, OType.STRING);
			vertexType.createProperty(ACCOUNT_IMAP_SERVER, OType.STRING);
			vertexType.createProperty(ACCOUNT_MAX_CONCURRENT_CONNECTIONS, OType.INTEGER);
			vertexType.createProperty(ACCOUNT_SIZE_MB, OType.INTEGER);
			vertexType.createProperty(ACCOUNT_ATTACHMENT_SIZE_MB, OType.INTEGER);
			vertexType.createProperty(ACCOUNT_CRYPTO_KEY, OType.STRING);
			vertexType.createIndex(createIndexName(Account.class, ACCOUNT_EMAIL),
				OClass.INDEX_TYPE.UNIQUE, ACCOUNT_EMAIL);
		}
		if (tx.getVertexType(File.class.getSimpleName()) == null) {
			OrientVertexType vertexType = tx.createVertexType(File.class.getSimpleName());
			vertexType.createProperty(FILE_UNIQUE_ID, OType.STRING);
			vertexType.createProperty(FILE_HASH, OType.STRING);
			vertexType.createProperty(FILE_NAME, OType.STRING);
			vertexType.createProperty(FILE_ABSOLUTE_PATH, OType.STRING);
			vertexType.createProperty(FILE_SIZE, OType.LONG);
			vertexType.createEdgeProperty(Direction.OUT, Account.class.getSimpleName(), OType.LINK);
			vertexType.createIndex(createIndexName(File.class, FILE_ABSOLUTE_PATH, FILE_HASH),
				OClass.INDEX_TYPE.UNIQUE, FILE_ABSOLUTE_PATH, FILE_HASH);
		}
		if (tx.getVertexType(FileChunk.class.getSimpleName()) == null) {
			OrientVertexType vertexType = tx.createVertexType(FileChunk.class.getSimpleName());
			vertexType.createProperty(FILE_CHUNK_UNIQUE_ID, OType.STRING);
			vertexType.createProperty(FILE_CHUNK_NUMBER, OType.STRING);
			vertexType.createProperty(FILE_CHUNK_HASH, OType.STRING);
			vertexType.createProperty(FILE_CHUNK_SIZE, OType.LONG);
			vertexType.createEdgeProperty(Direction.OUT, File.class.getSimpleName(), OType.LINK);
			vertexType.createIndex(createIndexName(FileChunk.class, FILE_CHUNK_UNIQUE_ID),
				OClass.INDEX_TYPE.UNIQUE, FILE_CHUNK_UNIQUE_ID);
		}
		tx.shutdown();
	}

	private String createIndexName(Class cls, String field) {
		return String.format("%s_%s", cls.getSimpleName(), field);
	}

	private String createIndexName(Class cls, String... fields) {
		StringBuilder sb = new StringBuilder(cls.getSimpleName());
		for (String field : fields) {
			sb.append('_').append(field);
		}
		return sb.toString();
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
