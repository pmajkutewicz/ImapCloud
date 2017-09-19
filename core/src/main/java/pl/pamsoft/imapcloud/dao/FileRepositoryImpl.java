package pl.pamsoft.imapcloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.File;

@Repository
public class FileRepositoryImpl implements FileRepositoryCustom {

	@Lazy
	@Autowired
	private FileRepository fileRepository;

	@Override
	public void markFileCompleted(Long id) {
		File file = fileRepository.getById(id);
		file.setCompleted(true);
		fileRepository.save(file);
	}

}
