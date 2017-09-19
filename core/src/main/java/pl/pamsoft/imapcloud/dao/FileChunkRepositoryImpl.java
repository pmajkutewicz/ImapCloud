package pl.pamsoft.imapcloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.List;

@Repository
public class FileChunkRepositoryImpl implements FileChunkRepositoryCustom {

	@Lazy
	@Autowired
	private FileChunkRepository fileChunkRepository;

	@Override
	public void markChunkVerified(Long chunkDbId, boolean isExist) {
		FileChunk chunk = fileChunkRepository.findOne(chunkDbId);
		chunk.setChunkExists(isExist);
		fileChunkRepository.save(chunk);
	}

	@Override
	public void deleteFileChunks(String fileUniqueId) {
		List<FileChunk> fileChunks = fileChunkRepository.getFileChunks(fileUniqueId);
		fileChunkRepository.deleteInBatch(fileChunks);
	}

}
