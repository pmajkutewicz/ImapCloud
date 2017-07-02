package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.exceptions.ChunkAlreadyExistException;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class FileServices {

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	public Collection<File> findUploadedFiles() {
		return fileRepository.findAll();
	}

	public File getFileByUniqueId(String fileUniqueId) throws FileNotFoundException {
		return fileRepository.getByFileUniqueId(fileUniqueId);
	}

	public File saveFile(UploadChunkContainer uploadChunkContainer, Account account) throws FileAlreadyExistsException {
		FileDto fileDto = uploadChunkContainer.getFileDto();
		String uniqueId = UUID.randomUUID().toString();

		File file = new File();
		file.setAbsolutePath(fileDto.getAbsolutePath());
		file.setName(fileDto.getName());
		file.setOwnerAccount(account);
		file.setFileUniqueId(uniqueId);
		file.setFileHash(uploadChunkContainer.getFileHash());
		file.setSize(fileDto.getSize());

		return fileRepository.save(file);
	}

	public FileChunk saveChunk(UploadChunkContainer uploadChunkContainer) throws ChunkAlreadyExistException {
		FileChunk chunk = new FileChunk();
		chunk.setFileChunkUniqueId(uploadChunkContainer.getFileChunkUniqueId());
		chunk.setOwnerFile(fileRepository.getById(uploadChunkContainer.getSavedFileId()));
		chunk.setSize((long) uploadChunkContainer.getData().length);
		chunk.setChunkNumber(uploadChunkContainer.getChunkNumber());
		chunk.setChunkHash(uploadChunkContainer.getChunkHash());
		chunk.setMessageId(uploadChunkContainer.getStorageChunkId());
		chunk.setLastChunk(uploadChunkContainer.isLastChunk());
		FileChunk fileChunk = fileChunkRepository.save(chunk);
		if (uploadChunkContainer.isLastChunk()) {
			fileRepository.markFileCompleted(uploadChunkContainer.getSavedFileId());
		}
		return fileChunk;
	}

	public List<FileChunk> getFileChunks(String fileUniqueId){
		return fileChunkRepository.getFileChunks(fileUniqueId);
	}

}
