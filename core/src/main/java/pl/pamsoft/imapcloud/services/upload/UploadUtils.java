package pl.pamsoft.imapcloud.services.upload;

import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.services.FilesIOService;

import java.util.ArrayList;
import java.util.List;

public class UploadUtils {

	//CSOFF: MagicNumber
	public static int toBytes(int maxChunkSizeMB) {
		return maxChunkSizeMB * 1024 * 1024;
	}
	//CSON: MagicNumber

	public static List<FileDto> parseDirectories(FilesIOService filesService, FileDto fileDto) {
		List<FileDto> result = new ArrayList<>();
		if (FileDto.FileType.FILE == fileDto.getType()) {
			result.add(fileDto);
		} else {
			for (FileDto dto : filesService.listFilesInDir(filesService.getFile(fileDto))) {
				if (FileDto.FileType.DIRECTORY == dto.getType()) {
					result.addAll(parseDirectories(filesService, dto));
				} else {
					result.add(dto);
				}
			}
		}
		return result;
	}

}
