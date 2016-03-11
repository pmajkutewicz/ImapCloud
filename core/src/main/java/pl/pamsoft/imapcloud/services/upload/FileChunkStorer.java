package pl.pamsoft.imapcloud.services.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.util.function.Function;

public class FileChunkStorer implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileChunkStorer.class);

	private FileServices fileServices;

	public FileChunkStorer(FileServices fileServices) {
		this.fileServices = fileServices;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer ucc) {
		LOG.debug("Saving chunk data for chunk {} of {}", ucc.getChunkNumber(), ucc.getFileDto().getName());
		FileChunk savedChunk = fileServices.saveChunk(ucc);
		return UploadChunkContainer.addChunkId(ucc, savedChunk.getFileChunkUniqueId());
	}
}
