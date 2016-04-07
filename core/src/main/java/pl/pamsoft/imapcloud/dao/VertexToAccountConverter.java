package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Vertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.Account;

import java.util.function.Function;

@Component
@SuppressFBWarnings("SCII_SPOILED_CHILD_INTERFACE_IMPLEMENTOR")
public class VertexToAccountConverter extends AbstractVertexConverter implements Function<Vertex, Account> {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public Account apply(Vertex v) {
		Account a = new Account();
		a.setId(v.getId().toString());
		a.setLogin(v.getProperty(GraphProperties.ACCOUNT_LOGIN));
		a.setEmail(v.getProperty(GraphProperties.ACCOUNT_EMAIL));
		a.setImapServerAddress(v.getProperty(GraphProperties.ACCOUNT_IMAP_SERVER));
		a.setPassword(v.getProperty(GraphProperties.ACCOUNT_PASSWORD));
		a.setMaxConcurrentConnections(v.getProperty(GraphProperties.ACCOUNT_MAX_CONCURRENT_CONNECTIONS));
		a.setSizeMB(v.getProperty(GraphProperties.ACCOUNT_SIZE_MB));
		a.setAttachmentSizeMB(v.getProperty(GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB));
		a.setCryptoKey(v.getProperty(GraphProperties.ACCOUNT_CRYPTO_KEY));
		return a;
	}
}