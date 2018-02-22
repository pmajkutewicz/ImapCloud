package pl.pamsoft.imapcloud.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long>, FileRepositoryCustom {

	File getByFileUniqueId(String fileUniqueId);

	File getById(Long savedFileId);

	File getByAbsolutePath(String absolutePath);
}
