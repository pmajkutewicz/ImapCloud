package pl.pamsoft.imapcloud.config;

import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.entity.FileProgress;
import pl.pamsoft.imapcloud.entity.TaskProgress;

import javax.annotation.PostConstruct;

@Component
public class OObjectDatabaseTxFactory {

	@Value("${spring.data.orient.url}")
	private String url;

	@Value("${spring.data.orient.username}")
	private String username;

	@Value("${spring.data.orient.password}")
	private String password;

	@PostConstruct
	public void config() {
		OObjectDatabaseTx db = OObjectDatabasePool.global().acquire(url, username, password);
		OEntityManager entityManager = db.getEntityManager();
		entityManager.registerEntityClass(TaskProgress.class);
		entityManager.registerEntityClass(FileProgress.class);
	}


	public OObjectDatabaseTx getObjectDB() {
		return OObjectDatabasePool.global().acquire(url, username, password);
	}
}
