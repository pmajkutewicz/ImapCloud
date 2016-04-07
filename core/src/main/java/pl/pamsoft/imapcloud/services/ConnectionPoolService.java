package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.imap.IMAPConnectionFactory;

import javax.mail.Store;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConnectionPoolService {

	private Map<String, GenericObjectPool<Store>> accountPoolMap = new ConcurrentHashMap<>();

	public GenericObjectPool<Store> getOrCreatePoolForAccount(Account account) {
		String key = account.getImapServerAddress();
		if (accountPoolMap.containsKey(key)) {
			return accountPoolMap.get(key);
		} else {
			IMAPConnectionFactory connectionFactory = new IMAPConnectionFactory(account.getLogin(), account.getPassword(), key);
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(account.getMaxConcurrentConnections());
			config.setTestOnBorrow(true);
			config.setTestOnReturn(true);
			config.setTestWhileIdle(true);
			GenericObjectPool<Store> pool = new GenericObjectPool<>(connectionFactory, config);
			accountPoolMap.put(key, pool);
			return pool;
		}
	}
}
