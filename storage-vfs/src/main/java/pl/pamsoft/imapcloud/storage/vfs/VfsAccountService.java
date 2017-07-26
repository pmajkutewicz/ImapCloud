package pl.pamsoft.imapcloud.storage.vfs;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.Account;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.api.accounts.ChunkDeleter;
import pl.pamsoft.imapcloud.api.accounts.ChunkDownloader;
import pl.pamsoft.imapcloud.api.accounts.ChunkRecoverer;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.api.accounts.ChunkVerifier;

import javax.annotation.PostConstruct;

@Service
public class VfsAccountService implements AccountService {

	private FileSystemManager fsManager;

	@PostConstruct
	public void init() throws FileSystemException {
		fsManager = VFS.getManager();
	}

	@Override
	public String getType() {
		return "vfs";
	}

	@Override
	public ChunkUploader getChunkUploader(Account account) {
		return new VfsChunkUploader(fsManager);
	}

	@Override
	public ChunkDownloader getChunkDownloader(Account account) {
		return new VfsChunkDownloader(fsManager);
	}

	@Override
	public ChunkDeleter getChunkDeleter(Account account) {
		return new VfsChunkDeleter(fsManager);
	}

	@Override
	public ChunkVerifier getChunkVerifier(Account account) {
		return new VfsChunkVerifier(fsManager);
	}

	@Override
	public ChunkRecoverer getChunkRecoverer(Account account) {
		return new VfsChunkRecoverer(fsManager);
	}
}
