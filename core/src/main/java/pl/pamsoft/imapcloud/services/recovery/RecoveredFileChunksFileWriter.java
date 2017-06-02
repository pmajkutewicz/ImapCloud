package pl.pamsoft.imapcloud.services.recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.function.Function;

public class RecoveredFileChunksFileWriter implements Function<RecoveryChunkContainer, RecoveryChunkContainer> {

	private final ObjectMapper objectMapper;
	private final String recoveries;
	private final FilesIOService filesIOService;

	public RecoveredFileChunksFileWriter(FilesIOService filesIOService, String recoveries) {
		this.filesIOService = filesIOService;
		this.recoveries = recoveries;
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public RecoveryChunkContainer apply(RecoveryChunkContainer rcc) {
		try {
			String fileName = String.format("%s.%s", rcc.getTaskId(), "ic");
			String data = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rcc);
			OutputStream os = filesIOService.getOutputStream(Paths.get(recoveries, fileName + ".zip"));
			filesIOService.packToFile(os, fileName, data);
			return rcc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RecoveryChunkContainer.EMPTY;
	}


}
