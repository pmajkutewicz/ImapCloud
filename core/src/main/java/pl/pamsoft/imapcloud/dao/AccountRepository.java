package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientDynaElementIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.exceptions.AccountAlreadyExistException;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

@Repository
public class AccountRepository extends AbstractRepository<Account> {

	private static final Logger LOG = LoggerFactory.getLogger(AccountRepository.class);
	private static final OSQLSynchQuery<ODocument> USED_SPACE_QUERY = new OSQLSynchQuery<>("select sum(size).asLong() as sum from FileChunk where OUT('is_part_of').OUT('is_owned_by')['login'] = :login");

	@Autowired
	private Function<Vertex, Account> converter;

	@Override
	@SuppressFBWarnings("CFS_CONFUSING_FUNCTION_SEMANTICS")
	public Account save(Account account) throws AccountAlreadyExistException {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
		Iterable<Vertex> storedFiles = graphDB.getVertices("Account.email", account.getLogin()); // TODO: key should be login + host
		Iterator<Vertex> iterator = storedFiles.iterator();
		if (!iterator.hasNext()) {
			OrientVertex orientVertex = graphDB.addVertex(
				"class:" + Account.class.getSimpleName(),
				GraphProperties.ACCOUNT_EMAIL, account.getLogin());
			fillProperties(orientVertex, account);
			updateIdAndVersionFields(account, orientVertex);
			graphDB.shutdown();
		} else {
			LOG.warn("Duplicate account: {}", account.getLogin());
			throw new AccountAlreadyExistException(account.getLogin());
		}
		return account;
	}

	public Long getUsedSpace(String accountId) {
		OrientDynaElementIterable result = getDb().getGraphDB().command(USED_SPACE_QUERY).execute(Collections.singletonMap("login", accountId));
		Iterator<Object> iterator = result.iterator();
		if (iterator.hasNext()) {
			OrientVertex next = (OrientVertex) iterator.next();
			return next.getProperty("sum");
		} else  {
			return 0L;
		}
	}

	private void updateIdAndVersionFields(Account account, Element orientVertex) {
		ORecordId id = (ORecordId) orientVertex.getId();
		account.setId(id.toString());
	}

	@SuppressFBWarnings("OCP_OVERLY_CONCRETE_PARAMETER")
	private void fillProperties(Element accountVertex, Account account) {
		accountVertex.setProperty(GraphProperties.ACCOUNT_LOGIN, account.getLogin());
		accountVertex.setProperty(GraphProperties.ACCOUNT_TYPE, account.getType());
		accountVertex.setProperty(GraphProperties.ACCOUNT_HOST, account.getHost());
		accountVertex.setProperty(GraphProperties.ACCOUNT_PASSWORD, account.getPassword());
		accountVertex.setProperty(GraphProperties.ACCOUNT_MAX_CONCURRENT_CONNECTIONS, account.getMaxConcurrentConnections());
		accountVertex.setProperty(GraphProperties.ACCOUNT_ACCOUNT_SIZE_MB, account.getAccountSizeMB());
		accountVertex.setProperty(GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB, account.getAttachmentSizeMB());
		accountVertex.setProperty(GraphProperties.ACCOUNT_CRYPTO_KEY, account.getCryptoKey());

		account.getAdditionalProperties().entrySet().forEach(p -> accountVertex.setProperty(p.getKey(), p.getValue()));
	}

	@Override
	public Function<Vertex, Account> getConverter() {
		return converter;
	}
}
