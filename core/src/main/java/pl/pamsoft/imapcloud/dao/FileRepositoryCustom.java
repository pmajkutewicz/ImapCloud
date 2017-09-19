package pl.pamsoft.imapcloud.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface FileRepositoryCustom {

	void markFileCompleted(Long id);

}
