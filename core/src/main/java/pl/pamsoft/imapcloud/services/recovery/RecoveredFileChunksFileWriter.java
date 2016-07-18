package pl.pamsoft.imapcloud.services.recovery;

import org.json.JSONObject;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.function.Function;

public class RecoveredFileChunksFileWriter implements Function<RecoveryChunkContainer, RecoveryChunkContainer> {

	private static final int INDENT_FACTOR = 2;
	private final String recoveries;
	private final FilesIOService filesIOService;

	public RecoveredFileChunksFileWriter(FilesIOService filesIOService, String recoveries) {
		this.filesIOService = filesIOService;
		this.recoveries = recoveries;
	}

	@Override
	public RecoveryChunkContainer apply(RecoveryChunkContainer rcc) {
		try {
			JSONObject jsonObject = new JSONObject(rcc);
			String fileName = String.format("%s.%s", rcc.getTaskId(), "ic");
			String data = jsonObject.toString(INDENT_FACTOR);
			OutputStream os = filesIOService.getOutputStream(Paths.get(recoveries, fileName + ".zip"));
			filesIOService.packToFile(os, fileName, data);
			return rcc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RecoveryChunkContainer.EMPTY;
	}


}
