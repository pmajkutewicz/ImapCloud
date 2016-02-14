package pl.pamsoft.imapcloud.services.upload;

import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.util.function.Function;

public class FileChunkStorer implements Function<UploadChunkContainer, UploadChunkContainer> {

	private FileServices fileServices;

	public FileChunkStorer(FileServices fileServices) {
		this.fileServices = fileServices;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer ucc) {
		FileChunk savedChunk = fileServices.saveChunk(ucc);
		return UploadChunkContainer.addChunkId(ucc, savedChunk.getFileChunkUniqueId());
	}
}
