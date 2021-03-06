package pl.pamsoft.imapcloud.storage.imap;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.Account;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.api.accounts.ChunkDeleter;
import pl.pamsoft.imapcloud.api.accounts.ChunkDownloader;
import pl.pamsoft.imapcloud.api.accounts.ChunkRecoverer;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.api.accounts.ChunkVerifier;

import javax.mail.Store;
import java.util.Collection;
import java.util.Collections;

@Service
public class ImapAccountService implements AccountService {

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Override
	public String getType() {
		return "imap";
	}

	@Override
	public Collection<String> getRequiredPropertiesNames() {
		return Collections.emptyList();
	}

	@Override
	public ChunkUploader getChunkUploader(Account account) {
		return new ImapChunkUploader(getPoolForAccount(account));
	}

	@Override
	public ChunkDownloader getChunkDownloader(Account account) {
		return new ImapChunkDownloader(getPoolForAccount(account));
	}

	@Override
	public ChunkDeleter getChunkDeleter(Account account) {
		return new ImapChunkDeleter(getPoolForAccount(account));
	}

	@Override
	public ChunkVerifier getChunkVerifier(Account account) {
		return new ImapChunkVerifier(getPoolForAccount(account));
	}

	@Override
	public ChunkRecoverer getChunkRecoverer(Account account) {
		return new ImapChunkRecoverer(getPoolForAccount(account));
	}

	private GenericObjectPool<Store> getPoolForAccount(Account account) {
		return connectionPoolService.getOrCreatePoolForAccount(account);
	}
}
