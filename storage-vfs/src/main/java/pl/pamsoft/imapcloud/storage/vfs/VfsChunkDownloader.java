package pl.pamsoft.imapcloud.storage.vfs;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import pl.pamsoft.imapcloud.api.accounts.ChunkDownloader;
import pl.pamsoft.imapcloud.api.containers.DownloadChunkContainer;

import java.io.IOException;
import java.io.InputStream;

public class VfsChunkDownloader implements ChunkDownloader {

	private FileSystemManager fsManager;

	public VfsChunkDownloader(FileSystemManager fsManager) {
		this.fsManager = fsManager;
	}

	@Override
	public byte[] download(DownloadChunkContainer dcc) throws IOException {
		FileObject fileObject = fsManager.resolveFile(dcc.getStorageChunkId());
		InputStream inputStream = fileObject.getContent().getInputStream();
		byte[] bytes = IOUtils.toByteArray(inputStream);
		fileObject.close();
		return bytes;
	}
}
