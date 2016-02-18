package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;

import java.nio.file.FileAlreadyExistsException;
import java.util.UUID;

@Service
public class FileServices {

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	public File saveFile(UploadChunkContainer uploadChunkContainer, Account account) throws FileAlreadyExistsException {
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

	public FileChunk saveChunk(UploadChunkContainer uploadChunkContainer) {
		FileChunk chunk = new FileChunk();
		chunk.setFileChunkUniqueId(createChunkId(uploadChunkContainer.getFileUniqueId(), uploadChunkContainer.getChunkNumber()));
		chunk.setOwnerFile(fileRepository.getById(uploadChunkContainer.getSavedFileId()));
		chunk.setSize((long) uploadChunkContainer.getData().length);
		chunk.setChunkNumber(uploadChunkContainer.getChunkNumber());
		chunk.setChunkHash(uploadChunkContainer.getChunkHash());
		chunk.setMessageId(uploadChunkContainer.getMessageId());
		return fileChunkRepository.save(chunk);
	}

	private String createChunkId(String fileId, int partNumber) {
		return String.format("%s.%04d", fileId, partNumber);
	}
}
