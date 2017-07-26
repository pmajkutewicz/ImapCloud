package pl.pamsoft.imapcloud.storage.vfs;

import org.apache.commons.vfs2.FileSystemManager;
import pl.pamsoft.imapcloud.api.accounts.ChunkVerifier;
import pl.pamsoft.imapcloud.api.containers.VerifyChunkContainer;

import java.io.IOException;

public class VfsChunkVerifier implements ChunkVerifier {

	private FileSystemManager fsManager;

	public VfsChunkVerifier(FileSystemManager fsManager) {
		this.fsManager = fsManager;
	}

	@Override
	public boolean verify(VerifyChunkContainer vcc) throws IOException {
		return fsManager.resolveFile(vcc.getStorageChunkId()).exists();
	}
}
