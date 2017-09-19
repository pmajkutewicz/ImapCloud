package pl.pamsoft.imapcloud.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface FileChunkRepositoryCustom {

	void markChunkVerified(Long chunkDbId, boolean isExist);

	void deleteFileChunks(String fileUniqueId);


}
