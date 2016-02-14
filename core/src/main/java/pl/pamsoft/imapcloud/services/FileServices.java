package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.util.UUID;

@Service
public class FileServices {

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	public File saveFile(UploadChunkContainer uploadChunkContainer, Account account) {
		FileDto fileDto = uploadChunkContainer.getFileDto();
		String uniqueId = UUID.randomUUID().toString();

		pl.pamsoft.imapcloud.entity.File file = new pl.pamsoft.imapcloud.entity.File();
		file.setAbsolutePath(fileDto.getAbsolutePath());
		file.setName(fileDto.getName());
		file.setOwnerAccount(account);
		file.setFileUniqueId(uniqueId);
		file.setFileHash(uploadChunkContainer.getFileHash());
		file.setSize(fileDto.getSize());

		return fileRepository.save(file);
	}

	public void saveChunk(UploadChunkContainer uploadChunkContainer) {
		String uniqueId = UUID.randomUUID().toString();
		FileChunk chunk = new FileChunk();
		chunk.setFileChunkUniqueId(uniqueId);
		chunk.setOwnerFile(fileRepository.getById(uploadChunkContainer.getSavedFileId()));
		chunk.setSize((long) uploadChunkContainer.getData().length);
		chunk.setChunkNumber(uploadChunkContainer.getChunkNumber());
		chunk.setChunkHash(uploadChunkContainer.getChunkHash());
		fileChunkRepository.save(chunk);
	}

}
