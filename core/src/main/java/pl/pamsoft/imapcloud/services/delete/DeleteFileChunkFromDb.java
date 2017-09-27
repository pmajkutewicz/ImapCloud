package pl.pamsoft.imapcloud.services.delete;

import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.containers.DeleteChunkContainer;

import java.util.function.Function;

public class DeleteFileChunkFromDb implements Function<DeleteChunkContainer, DeleteChunkContainer> {
	private FileChunkRepository fileChunkRepository;
	private FileRepository fileRepository;

	public DeleteFileChunkFromDb(FileChunkRepository fileChunkRepository, FileRepository fileRepository) {
		this.fileChunkRepository = fileChunkRepository;
		this.fileRepository = fileRepository;
	}

	@Override
	public DeleteChunkContainer apply(DeleteChunkContainer dcc) {
		if (dcc.getDeleted()) {
			// FIXME:this should delete one chunk... should be refactore - no delete chunk as for now
			fileChunkRepository.deleteFileChunks(dcc.getFileUniqueId());
			File file = fileRepository.getByFileUniqueId(dcc.getFileUniqueId());
			fileRepository.delete(file);
			return DeleteChunkContainer.markAsDeletedFromDB(dcc);
		}
		return DeleteChunkContainer.markAsNotDeletedFromDB(dcc);
	}
}
