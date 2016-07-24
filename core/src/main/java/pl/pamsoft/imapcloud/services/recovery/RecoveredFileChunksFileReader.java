package pl.pamsoft.imapcloud.services.recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Function;

public class RecoveredFileChunksFileReader implements Function<Path, RecoveryChunkContainer> {

	private final FilesIOService filesIOService;
	private final ObjectMapper mapper = new ObjectMapper();

	public RecoveredFileChunksFileReader(FilesIOService filesIOService) {
		this.filesIOService = filesIOService;
	}

	@Override
	public RecoveryChunkContainer apply(Path path) {
		try {
			InputStream is = filesIOService.getInputStream(path.toFile());
			return mapper.readValue(filesIOService.unPack(is), RecoveryChunkContainer.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return RecoveryChunkContainer.EMPTY;
	}


}
