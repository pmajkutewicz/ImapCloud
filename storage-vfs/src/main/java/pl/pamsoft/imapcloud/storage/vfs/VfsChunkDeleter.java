package pl.pamsoft.imapcloud.storage.vfs;

import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemManager;
import pl.pamsoft.imapcloud.api.accounts.ChunkDeleter;
import pl.pamsoft.imapcloud.api.containers.DeleteChunkContainer;

import java.io.IOException;

public class VfsChunkDeleter implements ChunkDeleter {

	private static final String TMP_IC = "/tmp/ic/";
	private FileSystemManager fsManager;

	public VfsChunkDeleter(FileSystemManager fsManager) {
		this.fsManager = fsManager;
	}

	@Override
	public boolean delete(DeleteChunkContainer dcc) throws IOException {
		String folderName = VfsUtils.createFolderName(dcc.getFileHash());
		String filePath = String.format("vfs:///%s/%s/", TMP_IC, folderName);

		return 0 < fsManager.resolveFile(filePath).delete(new FileSelector() {
			@Override
			public boolean includeFile(FileSelectInfo fileInfo) {
				return fileInfo.getFile().getName().getBaseName().startsWith(dcc.getFileUniqueId()+'.');
			}

			@Override
			public boolean traverseDescendents(FileSelectInfo fileInfo) {
				return true;
			}
		});
	}
}
