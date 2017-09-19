package pl.pamsoft.imapcloud.rest;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
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
import pl.pamsoft.imapcloud.services.DeletionService;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.UploadService;
import pl.pamsoft.imapcloud.services.VerificationService;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("uploaded")
public class UploadedFileRestController {

	@Autowired
	private FileServices fileServices;

	@Autowired
	private VerificationService verificationService;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private DeletionService deletionService;

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
		ufcd.setSize(fileChunk.getOrgSize());
		ufcd.setFileChunkUniqueId(fileChunk.getFileChunkUniqueId());
		ufcd.setChunkHash(fileChunk.getChunkHash());
		ufcd.setChunkExists(fileChunk.getChunkExists());
		ufcd.setLastVerifiedAt(fileChunk.getLastVerifiedAt());
		return ufcd;
	};

	@ApiOperation("Get list of uploaded files")
	@RequestMapping(value = "files", method = RequestMethod.GET)
	public UploadedFilesResponse getUploadedFiles() {
		Collection<File> uploadedFiles = fileServices.findUploadedFiles();
		List<UploadedFileDto> uploadedFileDtos = uploadedFiles.stream()
			.map(converter)
			.collect(Collectors.toList());

		return new UploadedFilesResponse(uploadedFileDtos);
	}

	@ApiOperation("Get list of uploaded chunk for given file")
	@RequestMapping(value = "chunks", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UploadedFileChunksResponse getUploadedChunks(@RequestParam(name = "fileId") String fileUniqueId) {
		if (StringUtils.isEmpty(fileUniqueId)) {
			return new UploadedFileChunksResponse(Collections.emptyList());
		}
		List<FileChunk> fileChunks = fileServices.getFileChunks(fileUniqueId);
		List<UploadedFileChunkDto> converted = fileChunks.stream().map(toUploadedFileChunkDtoConverter).collect(Collectors.toList());
		return new UploadedFileChunksResponse(converted);
	}

	@ApiOperation("Verify selected file")
	@RequestMapping(value = "verify", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void verifyFile(@RequestParam(name = "fileId") String fileUniqueId) {
		List<FileChunk> fileChunks = fileServices.getFileChunks(fileUniqueId);
		verificationService.validate(fileChunks);
	}

	@ApiOperation("Resume uploading selected file")
	@RequestMapping(value = "resume", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void resumeFile(@RequestParam(name = "fileId") String fileUniqueId) {
		try {
			File fileByUniqueId = fileServices.getFileByUniqueId(fileUniqueId);
			uploadService.resume(fileByUniqueId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@ApiOperation("Delete selected file")
	@RequestMapping(value = "delete", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void deleteFile(@RequestParam(name = "fileId") String fileUniqueId) {
		try {
			File fileByUniqueId = fileServices.getFileByUniqueId(fileUniqueId);
			deletionService.delete(fileByUniqueId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
