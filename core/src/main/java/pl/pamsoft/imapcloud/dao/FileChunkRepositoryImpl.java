package pl.pamsoft.imapcloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.List;
import java.util.Optional;

@Repository
public class FileChunkRepositoryImpl implements FileChunkRepositoryCustom {

	@Lazy
	@Autowired
	private FileChunkRepository fileChunkRepository;

	@Override
	public void markChunkVerified(Long chunkDbId, boolean isExist) {
		Optional<FileChunk> chunk = fileChunkRepository.findById(chunkDbId);
		if (chunk.isPresent()) {
			chunk.get().setChunkExists(isExist);
			fileChunkRepository.save(chunk.get());
		}
	}

	@Override
	public void deleteFileChunks(String fileUniqueId) {
		List<FileChunk> fileChunks = fileChunkRepository.getFileChunks(fileUniqueId);
		fileChunkRepository.deleteInBatch(fileChunks);
	}

}
