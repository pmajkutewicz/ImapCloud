package pl.pamsoft.imapcloud.services;

import pl.pamsoft.imapcloud.dto.FileDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DirectoryProcessor implements Function<FileDto, Stream<FileDto>> {

	private FilesService filesService;

	public DirectoryProcessor(FilesService filesService) {
		this.filesService = filesService;
	}

	@Override
	public Stream<FileDto> apply(FileDto fileDto) {
		if (FileDto.Type.DIRECTORY == fileDto.getType()) {
			// user recursion to parse every subdirectory and extract files
			//return filesService.listFilesInDir(new File(fileDto.getAbsolutePath())).stream();
			List<FileDto> dtos = parseDirectories(fileDto);
			return dtos.stream();
		} else {
			return Stream.of(fileDto);
		}
	}

	private List<FileDto> parseDirectories(FileDto fileDto) {
		List<FileDto> result = new ArrayList<>();

		for (FileDto dto : filesService.listFilesInDir(new File(fileDto.getAbsolutePath()))) {
			if (FileDto.Type.DIRECTORY == dto.getType()) {
				result.addAll(parseDirectories(dto));
			} else {
				result.add(dto);
			}
		}
		return result;
	}
}
