package pl.pamsoft.imapcloud.storage.vfs;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import pl.pamsoft.imapcloud.api.accounts.ChunkUploader;
import pl.pamsoft.imapcloud.api.containers.UploadChunkContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class VfsChunkUploader implements ChunkUploader {

	private static final boolean NO_APPEND = false;
	private static final String TMP_IC = "/tmp/ic/";
	private FileSystemManager fsManager;

	public VfsChunkUploader(FileSystemManager fsManager) {
		this.fsManager = fsManager;
	}

	@Override
	public String upload(UploadChunkContainer ucc, Map<String, String> metadata) throws IOException {
		String folderName = VfsUtils.createFolderName(ucc.getFileHash());
		String fileName = VfsUtils.createFileName(ucc.getFileUniqueId(), ucc.getChunkNumber());
		String filePath = String.format("vfs:///%s/%s/%s", TMP_IC, folderName, fileName);

		FileObject file = fsManager.resolveFile(filePath);

		OutputStream outputStream = file.getContent().getOutputStream(NO_APPEND);

		outputStream.write(ucc.getData());
		outputStream.close();

		writeMetadata(ucc, metadata);

		return filePath;
	}


	private void writeMetadata(UploadChunkContainer ucc, Map<String, String> metadata) throws IOException {
		String folderName = VfsUtils.createFolderName(ucc.getFileHash());
		String fileName = createMetaFileName(ucc.getFileUniqueId(), ucc.getChunkNumber());
		String filePath = String.format("vfs:///%s/%s/%s", TMP_IC, folderName, fileName);

		FileObject file = fsManager.resolveFile(filePath);

		OutputStream outputStream = file.getContent().getOutputStream(NO_APPEND);

		List<String> lines = metadata.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(toList());
		IOUtils.writeLines(lines, "\r\n", outputStream, StandardCharsets.UTF_8);

		outputStream.close();
	}

	private String createMetaFileName(String fileName, int partNumber) {
		return String.format("%s.txt", VfsUtils.createFileName(fileName, partNumber));
	}
}
