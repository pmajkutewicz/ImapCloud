package pl.pamsoft.imapcloud.services;

import com.google.common.collect.Ordering;
import com.google.common.io.Files;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static pl.pamsoft.imapcloud.dto.FileDto.Type.DIRECTORY;
import static pl.pamsoft.imapcloud.dto.FileDto.Type.FILE;

@Service
public class FilesService {

	private Function<File, FileDto> toFileDto = f -> new FileDto(f.getName(), f.getAbsolutePath(), f.isDirectory() ? DIRECTORY : FILE, f.isDirectory() ? null : f.length());
	private Comparator<FileDto> dirFirstSorter = (f1, f2) -> ((DIRECTORY == f1.getType()) && (FILE == f2.getType())) ? -1 : ((f1.getType() == f2.getType()) ? 0 : 1);
	private Comparator<FileDto> byNameSorter = (f1, f2) -> String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());

	public List<FileDto> listFilesInDir(File dir) {
		File[] filesInDir = dir.listFiles();
		if (filesInDir != null) {
			return Arrays.stream(filesInDir)
				.map(toFileDto)
				.sorted(Ordering.from(dirFirstSorter).compound(byNameSorter))
				.collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	public long calculateDirSize(File directory) {
		Iterable<File> files = Files.fileTreeTraverser().breadthFirstTraversal(directory);
		return StreamSupport.stream(files.spliterator(), false).mapToLong(File::length).sum();
	}
}
