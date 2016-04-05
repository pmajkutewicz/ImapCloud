package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileChunkDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.responses.UploadedFileChunksResponse;
import pl.pamsoft.imapcloud.responses.UploadedFilesResponse;
import pl.pamsoft.imapcloud.services.FileServices;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("uploaded")
public class UploadedFileRestController {

	@Autowired
	private FileServices fileServices;

	private Function<File, UploadedFileDto> converter = file -> {
		UploadedFileDto uploadedFileDto = new UploadedFileDto();
		uploadedFileDto.setName(file.getName());
		uploadedFileDto.setAbsolutePath(file.getAbsolutePath());
		uploadedFileDto.setSize(file.getSize());
		uploadedFileDto.setCompleted(file.isCompleted());
		uploadedFileDto.setType(FileDto.FileType.FILE);
		uploadedFileDto.setFileUniqueId(file.getFileUniqueId());
		return uploadedFileDto;
	};

	private Function<FileChunk, UploadedFileChunkDto> toUploadedFileChunkDtoConverter = fileChunk -> {
		UploadedFileChunkDto ufcd = new UploadedFileChunkDto();
		ufcd.setMessageId(fileChunk.getMessageId());
		ufcd.setChunkNumber(fileChunk.getChunkNumber());
		ufcd.setSize(fileChunk.getSize());
		ufcd.setFileChunkUniqueId(fileChunk.getFileChunkUniqueId());
		ufcd.setChunkHash(fileChunk.getChunkHash());
		ufcd.setChunkExists(fileChunk.isChunkExists());
		ufcd.setLastVerifiedAt(fileChunk.getLastVerifiedAt());
		return ufcd;
	};

	@RequestMapping(value = "files", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UploadedFilesResponse getUploadedFiles() {
		Collection<File> uploadedFiles = fileServices.findUploadedFiles();
		List<UploadedFileDto> uploadedFileDtos = uploadedFiles.stream()
			.map(converter)
			.collect(Collectors.toList());

		return new UploadedFilesResponse(uploadedFileDtos);
	}

	@RequestMapping(value = "chunks", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UploadedFileChunksResponse getUploadedChunks(@RequestParam(name = "fileId") String fileUniqueId) {
		List<FileChunk> fileChunks = fileServices.getFileChunks(fileUniqueId);
		List<UploadedFileChunkDto> converted = fileChunks.stream().map(toUploadedFileChunkDtoConverter).collect(Collectors.toList());
		return new UploadedFileChunksResponse(converted);
	}

	@RequestMapping(value = "verify", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void verifyFile(@RequestParam(name = "fileId") String fileUniqueId) {
		List<FileChunk> fileChunks = fileServices.getFileChunks(fileUniqueId);
		System.out.println(fileChunks);
	}

}
