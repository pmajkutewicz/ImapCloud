package pl.pamsoft.imapcloud.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.List;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunk, Long>, FileChunkRepositoryCustom {

	@Query("select fc from FileChunk fc where fc.ownerFile.fileUniqueId = :fileUniqueId")
	List<FileChunk> getFileChunks(@Param("fileUniqueId") String fileUniqueId);

	@Query("select fc from FileChunk fc where fc.ownerFile.ownerAccount.id = :accountId")
	List<FileChunk> getFileChunksByAccountId(@Param("accountId") Long accountId);
}
