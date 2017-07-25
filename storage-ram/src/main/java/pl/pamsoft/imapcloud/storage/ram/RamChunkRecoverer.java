package pl.pamsoft.imapcloud.storage.ram;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileExtensionSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import pl.pamsoft.imapcloud.api.accounts.ChunkRecoverer;
import pl.pamsoft.imapcloud.api.containers.RecoveryChunkContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RamChunkRecoverer implements ChunkRecoverer {

	private static final int EXTENSION_LENGTH = 4;
	private static final String TMP_IC = "/tmp/ic/";
	private FileSystemManager fsManager;

	public RamChunkRecoverer(FileSystemManager fsManager) {
		this.fsManager = fsManager;
	}

	@Override
	public List<Map<String, String>> recover(RecoveryChunkContainer rcc) throws IOException {
		List<Map<String, String>> results = new ArrayList<>();
		String filePath = String.format("ram:///%s", TMP_IC);
		FileObject[] metas = fsManager.resolveFile(filePath).findFiles(new FileExtensionSelector("txt"));

		if (null != metas) {
			for (FileObject meta : metas) {
				String baseName = meta.getName().getBaseName();
				FileObject fileObject = fsManager.resolveFile(String.format("%s/%s", meta.getName().getParent(), baseName.substring(0, baseName.length() - EXTENSION_LENGTH)));
				if (fileObject.exists()) {
					List<String> lines = IOUtils.readLines(meta.getContent().getInputStream(), StandardCharsets.UTF_8);
					Map<String, String> chunkMeta = lines.stream().map(i -> Arrays.asList(i.split("="))).collect(Collectors.toMap(i -> i.get(0), i -> i.get(1)));
					chunkMeta.put("size", String.valueOf(fileObject.getContent().getSize()));
					chunkMeta.put("Message-ID", fileObject.getName().toString());
					results.add(chunkMeta);
				}
			}
		}

		return results;
	}
}
