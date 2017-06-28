package pl.pamsoft.imapcloud.imap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.api.accounts.ChunkDeleter;
import pl.pamsoft.imapcloud.api.accounts.ChunkDownloader;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.services.ConnectionPoolService;

@Service
public class ImapAccountService implements AccountService {

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Override
	public String getType() {
		return "imap";
	}

	@Override
	public ChunkUploader getChunkUploader(Account account) {
		return new ImapChunkUploader(connectionPoolService.getOrCreatePoolForAccount(account));
	}

	@Override
	public ChunkDownloader getChunkDownloader(Account account) {
		return new ImapChunkDownloader(connectionPoolService.getOrCreatePoolForAccount(account));
	}

	@Override
	public ChunkDeleter getChunkDeleter(Account account) {
		return new ImapChunkDeleter(connectionPoolService.getOrCreatePoolForAccount(account));
	}
}
