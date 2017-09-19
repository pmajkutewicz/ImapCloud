package pl.pamsoft.imapcloud.services.delete;

import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.services.containers.DeleteChunkContainer;

import java.util.function.Function;

public class DeleteFileChunkFromDb implements Function<DeleteChunkContainer, DeleteChunkContainer> {
	private FileChunkRepository fileChunkRepository;

	public DeleteFileChunkFromDb(FileChunkRepository fileChunkRepository) {
		this.fileChunkRepository = fileChunkRepository;
	}

	@Override
	public DeleteChunkContainer apply(DeleteChunkContainer dcc) {
		if (dcc.getDeleted()) {
			// TODO:this should delete one chunk... should be refactore - no delete chunk as for now
			fileChunkRepository.deleteFileChunks(dcc.getFileUniqueId());
			return DeleteChunkContainer.markAsDeletedFromDB(dcc);
		}
		return DeleteChunkContainer.markAsNotDeletedFromDB(dcc);
	}
}
