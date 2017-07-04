package pl.pamsoft.imapcloud.storage.ram;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.api.containers.UploadChunkContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class RamChunkUploader implements ChunkUploader {

	private static final boolean NO_APPEND = false;
	private FileSystemManager fsManager;

	public RamChunkUploader(FileSystemManager fsManager) {
		this.fsManager = fsManager;
	}

	@Override
	public String upload(UploadChunkContainer ucc, Map<String, String> metadata) throws IOException {
		String folderName = RamUtils.createFolderName(ucc.getFileHash());
		String fileName = createFileName(ucc.getFileUniqueId(), ucc.getChunkNumber());
		String filePath = String.format("ram:///%s/%s", folderName, fileName);

		FileObject file = fsManager.resolveFile(filePath);

		OutputStream outputStream = file.getContent().getOutputStream(NO_APPEND);

		outputStream.write(ucc.getData());
		outputStream.close();

		return filePath;
	}


	private String createFileName(String fileName, int partNumber) {
		return String.format("%s.%05d", fileName, partNumber);
	}
}
