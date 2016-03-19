package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.entity.File;
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
		return uploadedFileDto;
	};

	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UploadedFilesResponse getUploadedFiles() {
		Collection<File> uploadedFiles = fileServices.findUploadedFiles();
		List<UploadedFileDto> uploadedFileDtos = uploadedFiles.stream()
			.map(converter)
			.collect(Collectors.toList());

		return new UploadedFilesResponse(uploadedFileDtos);
	}

}
