package pl.pamsoft.imapcloud.storage.testing;

import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.Account;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.api.accounts.ChunkDeleter;
import pl.pamsoft.imapcloud.api.accounts.ChunkDownloader;
import pl.pamsoft.imapcloud.api.accounts.ChunkRecoverer;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.api.accounts.ChunkVerifier;

import java.util.Collection;
import java.util.Collections;

@Service
public class TestingAccountService implements AccountService {

	@Override
	public String getType() {
		return "ic-testing";
	}

	@Override
	public Collection<String> getRequiredPropertiesNames() {
		return Collections.emptyList();
	}

	@Override
	public ChunkUploader getChunkUploader(Account account) {
		return new TestingChunkUploader();
	}

	@Override
	public ChunkDownloader getChunkDownloader(Account account) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ChunkDeleter getChunkDeleter(Account account) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ChunkVerifier getChunkVerifier(Account account) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ChunkRecoverer getChunkRecoverer(Account account) {
		throw new RuntimeException("Not implemented");
	}

}
