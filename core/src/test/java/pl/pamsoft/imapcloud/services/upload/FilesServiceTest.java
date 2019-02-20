package pl.pamsoft.imapcloud.services.upload;

import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.services.FilesIOService;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FilesServiceTest {

	private static final int KIBIBYTE = 1024;
	private FilesIOService fs = new FilesIOService();

	@Test
	void traverseDirTest() {
		List<FileDto> files = fs.listFilesInDir(new File("").getAbsoluteFile());
		FileDto pom = getPom(files);
		assertNotNull(pom);
		assertEquals(FileDto.FileType.FILE, pom.getType());
		assertNotNull(pom.getSize());
		assertNotEquals(pom.getSize(), 0);
	}

	@Test
	void calculateDirSize() {
		long size = fs.calculateDirSize(new File(".").getAbsoluteFile());
		assertNotEquals(size, 0);
		assertTrue(size > KIBIBYTE);
	}

	private FileDto getPom(List<FileDto> files) {
		for (FileDto file : files) {
			if ("pom.xml".equals(file.getName())) {
				return file;
			}
		}
		return null;
	}
}
