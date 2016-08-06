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
	private static final OSQLSynchQuery<ODocument> USED_SPACE_QUERY = new OSQLSynchQuery<>("select sum(IN('is_owned_by').in('is_part_of').size) as sum from Account where @rid = :id");

	@Autowired
	private Function<Vertex, Account> converter;

	@Override
	@SuppressFBWarnings("CFS_CONFUSING_FUNCTION_SEMANTICS")
	public Account save(Account account) throws AccountAlreadyExistException {
		OrientGraphNoTx graphDB = getDb().getGraphDB();
		Iterable<Vertex> storedFiles = graphDB.getVertices("Account.email", account.getEmail());
		Iterator<Vertex> iterator = storedFiles.iterator();
		if (!iterator.hasNext()) {
			OrientVertex orientVertex = graphDB.addVertex(
				"class:" + Account.class.getSimpleName(),
				GraphProperties.ACCOUNT_EMAIL, account.getEmail());
			fillProperties(orientVertex, account);
			updateIdAndVersionFields(account, orientVertex);
			graphDB.shutdown();
		} else {
			LOG.warn("Duplicate account: {}", account.getEmail());
			throw new AccountAlreadyExistException(account.getEmail());
		}
		return account;
	}

	public Long getUsedSpace(String accountId) {
		OrientDynaElementIterable result = getDb().getGraphDB().command(USED_SPACE_QUERY).execute(Collections.singletonMap("id", accountId));
		Iterator<Object> iterator = result.iterator();
		if (iterator.hasNext()) {
			OrientVertex next = (OrientVertex) iterator.next();
			Long sum = next.getProperty("sum");
			LOG.debug("Account {} have {} occupied disk space.", accountId, sum);
			return sum;
		} else  {
			return 0L;
		}
	}

	private void updateIdAndVersionFields(Account account, Element orientVertex) {
		ORecordId id = (ORecordId) orientVertex.getId();
		account.setId(id.toString());
	}

	private void fillProperties(Element accountVertex, Account account) {
		accountVertex.setProperty(GraphProperties.ACCOUNT_LOGIN, account.getLogin());
		accountVertex.setProperty(GraphProperties.ACCOUNT_IMAP_SERVER, account.getImapServerAddress());
		accountVertex.setProperty(GraphProperties.ACCOUNT_PASSWORD, account.getPassword());
		accountVertex.setProperty(GraphProperties.ACCOUNT_MAX_CONCURRENT_CONNECTIONS, account.getMaxConcurrentConnections());
		accountVertex.setProperty(GraphProperties.ACCOUNT_SIZE_MB, account.getSizeMB());
		accountVertex.setProperty(GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB, account.getAttachmentSizeMB());
		accountVertex.setProperty(GraphProperties.ACCOUNT_CRYPTO_KEY, account.getCryptoKey());
	}

	@Override
	public Function<Vertex, Account> getConverter() {
		return converter;
	}
}
