package pl.pamsoft.imapcloud.services.recovery;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

public class RecoveredFileChunksFileWriter implements Function<RecoveryChunkContainer, RecoveryChunkContainer> {

	private static final int INDENT_FACTOR = 2;
	private final FilesIOService filesIOService;

	public RecoveredFileChunksFileWriter(FilesIOService filesIOService) {
		this.filesIOService = filesIOService;
	}

	@Override
	public RecoveryChunkContainer apply(RecoveryChunkContainer rcc) {
		try {
			JSONObject jsonObject = new JSONObject(rcc);
			String fileNameWithPath = String.format("%s.%s", rcc.getTaskId(), ".ic");
			String data = jsonObject.toString(INDENT_FACTOR);
			OutputStream os = filesIOService.getFileOutputStream(fileNameWithPath);
			IOUtils.write(data, os, StandardCharsets.UTF_8);
			IOUtils.closeQuietly(os);
			return rcc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RecoveryChunkContainer.EMPTY;
	}
}
