package pl.pamsoft.imapcloud.imap;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class IMAPConnectionFactory implements PooledObjectFactory<Store> {

	private static final Logger LOG = LoggerFactory.getLogger(IMAPConnectionFactory.class);

	private final String username;
	private final String password;
	private final String host;

	public IMAPConnectionFactory(String username, String password, String host) {
		this.username = username;
		this.password = password;
		this.host = host;
	}

	@Override
	public PooledObject<Store> makeObject() throws Exception {
		Properties props = new Properties();
		props.put("mail.store.protocol", "imaps");

		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("imaps");
		store.connect(host, username, password);
		return new DefaultPooledObject<>(store);
	}

	@Override
	public void destroyObject(PooledObject<Store> pooledObject) throws Exception {
		try {
			pooledObject.getObject().close();
		} catch (MessagingException e) {
			LOG.warn("Can't close imap client.");
		}
	}

	@Override
	public boolean validateObject(PooledObject<Store> pooledObject) {
		return pooledObject.getObject().isConnected();
	}

	@Override
	public void activateObject(PooledObject<Store> pooledObject) throws Exception {

	}

	@Override
	public void passivateObject(PooledObject<Store> pooledObject) throws Exception {

	}
}
