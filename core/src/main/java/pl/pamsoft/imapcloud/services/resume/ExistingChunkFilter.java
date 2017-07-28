package pl.pamsoft.imapcloud.services.resume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExistingChunkFilter implements Predicate<UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(ExistingChunkFilter.class);

	private Map<Integer, String> chunkHashMap;

	public ExistingChunkFilter(String fileUniqueId, FileServices fileServices) {
		List<FileChunk> fileChunks = fileServices.getFileChunks(fileUniqueId);
		chunkHashMap = fileChunks.stream().collect(Collectors.toMap(FileChunk::getChunkNumber, FileChunk::getChunkHash));
	}

	@Override
	public boolean test(UploadChunkContainer ucc) {
		int currentChunk = ucc.getChunkNumber();
		if (chunkHashMap.containsKey(currentChunk)) {
			boolean validHash = ucc.getChunkHash().equals(chunkHashMap.get(currentChunk));
			if (validHash) {
				LOG.debug("Chunk {} of {} already uploaded, skipping", ucc.getChunkNumber(), ucc.getFileDto().getAbsolutePath());
				return false;
			}
		}
		return true;
	}
}
