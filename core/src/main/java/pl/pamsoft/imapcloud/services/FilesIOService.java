package pl.pamsoft.imapcloud.services;

import com.google.common.collect.Ordering;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static pl.pamsoft.imapcloud.dto.FileDto.FileType.DIRECTORY;
import static pl.pamsoft.imapcloud.dto.FileDto.FileType.FILE;

@Service
public class FilesIOService {

	private static final int MAX_COMPRESSION = 9;

	private Function<File, FileDto> toFileDto = f -> new FileDto(f.getName(), f.getAbsolutePath(), f.isDirectory() ? DIRECTORY : FILE, f.isDirectory() ? null : f.length());
	private Comparator<FileDto> dirFirstSorter = (f1, f2) -> ((DIRECTORY == f1.getType()) && (FILE == f2.getType())) ? -1 : ((f1.getType() == f2.getType()) ? 0 : 1);
	private Comparator<FileDto> byNameSorter = (f1, f2) -> String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public File getFile(FileDto fileDto) {
		return new File(fileDto.getAbsolutePath());
	}

	public InputStream getInputStream(File file) throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(file));
	}
	public OutputStream getOutputStream(Path nameWithPath) throws IOException {
		return new BufferedOutputStream(FileUtils.openOutputStream(nameWithPath.toFile()));
	}

	public void packToFile(OutputStream os, String fileName, String data) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.setLevel(MAX_COMPRESSION);
		ZipEntry entry = new ZipEntry(fileName);
		entry.setSize(data.length());
		entry.setCrc(getCrc(data));
		zos.putNextEntry(entry);
		IOUtils.write(data, zos, StandardCharsets.UTF_8);
		zos.closeEntry();
		IOUtils.closeQuietly(zos);
	}

	public String unPack(InputStream is) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(is);
		ZipEntry nextEntry = zipInputStream.getNextEntry();
		if (null != nextEntry) {
			return IOUtils.toString(zipInputStream, StandardCharsets.UTF_8);
		}
		return null;
	};

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
		Iterable<File> files = com.google.common.io.Files.fileTraverser().breadthFirst(directory);
		return StreamSupport.stream(files.spliterator(), false).mapToLong(File::length).sum();
	}

	private long getCrc(String data) {
		CRC32 crc32 = new CRC32();
		crc32.update(data.getBytes(StandardCharsets.UTF_8));
		return crc32.getValue();
	}
}
