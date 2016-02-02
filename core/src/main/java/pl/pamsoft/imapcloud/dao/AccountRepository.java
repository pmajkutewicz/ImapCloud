package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.config.ODB;
import pl.pamsoft.imapcloud.entity.Account;

import java.util.Collection;
import java.util.LinkedList;

@Repository
public class AccountRepository {

	@Autowired
	private ODB db;

	public Account save(Account account) {
		return db.getDb().save(account);
	}

	public Account getById(String accountId) {
		return db.getDb().load(new ORecordId(accountId));
	}

	public Collection<Account> findAll() {
		OObjectIteratorClass<Account> accounts = db.getDb().browseClass(Account.class);
		Collection<Account> result = new LinkedList<>();
		accounts.forEachRemaining(result::add);
		return result;
	}
}
