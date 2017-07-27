package pl.pamsoft.imapcloud.dao;

import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Vertex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.entity.Account;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@SuppressFBWarnings("SCII_SPOILED_CHILD_INTERFACE_IMPLEMENTOR")
public class VertexToAccountConverter extends AbstractVertexConverter implements Function<Vertex, Account> {

	private Set<String> standardAccountProperties = Sets.newHashSet(GraphProperties.ACCOUNT_LOGIN, GraphProperties.ACCOUNT_TYPE, GraphProperties.ACCOUNT_HOST,
		GraphProperties.ACCOUNT_PASSWORD, GraphProperties.ACCOUNT_MAX_CONCURRENT_CONNECTIONS, GraphProperties.ACCOUNT_ACCOUNT_SIZE_MB,
		GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB, GraphProperties.ACCOUNT_CRYPTO_KEY, GraphProperties.ACCOUNT_EMAIL);

	@Override
	public Account apply(Vertex v) {
		Account a = new Account();
		a.setId(v.getId().toString());
		a.setLogin(v.getProperty(GraphProperties.ACCOUNT_LOGIN));
		a.setType(v.getProperty(GraphProperties.ACCOUNT_TYPE));
		a.setHost(v.getProperty(GraphProperties.ACCOUNT_HOST));
		a.setPassword(v.getProperty(GraphProperties.ACCOUNT_PASSWORD));
		a.setMaxConcurrentConnections(v.getProperty(GraphProperties.ACCOUNT_MAX_CONCURRENT_CONNECTIONS));
		a.setAccountSizeMB(v.getProperty(GraphProperties.ACCOUNT_ACCOUNT_SIZE_MB));
		a.setAttachmentSizeMB(v.getProperty(GraphProperties.ACCOUNT_ATTACHMENT_SIZE_MB));
		a.setCryptoKey(v.getProperty(GraphProperties.ACCOUNT_CRYPTO_KEY));

		Set<String> additionalPropsKeys = v.getPropertyKeys().stream().filter(k -> !standardAccountProperties.contains(k)).collect(Collectors.toSet());
		Map<String, String> props = additionalPropsKeys.stream().collect(Collectors.toMap(Function.identity(), v::getProperty));
		a.setAdditionalProperties(props);
		return a;
	}
}
