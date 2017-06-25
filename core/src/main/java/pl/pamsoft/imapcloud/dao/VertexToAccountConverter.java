package pl.pamsoft.imapcloud.dao;

import com.tinkerpop.blueprints.Vertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.Account;

import java.util.function.Function;

@Component
@SuppressFBWarnings("SCII_SPOILED_CHILD_INTERFACE_IMPLEMENTOR")
public class VertexToAccountConverter extends AbstractVertexConverter implements Function<Vertex, Account> {

	@Override
	public Account apply(Vertex v) {
		Account a = new Account();
		a.setId(v.getId().toString());
		a.setLogin(v.getProperty(GraphProperties.ACCOUNT_LOGIN));
		a.setHost(v.getProperty(GraphProperties.ACCOUNT_HOST));
		a.setPassword(v.getProperty(GraphProperties.ACCOUNT_PASSWORD));
		a.setMaxConcurrentConnections(v.getProperty(GraphProperties.ACCOUNT_MAX_CONCURRENT_CONNECTIONS));
		a.setAccountSizeMB(v.getProperty(GraphProperties.ACCOUNT_ACCOUNT_SIZE_MB));
		a.setAttachmentSizeMB(v.getProperty(GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB));
		a.setCryptoKey(v.getProperty(GraphProperties.ACCOUNT_CRYPTO_KEY));
		return a;
	}
}
