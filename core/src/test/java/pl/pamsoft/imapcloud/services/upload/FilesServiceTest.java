package pl.pamsoft.imapcloud.services.upload;

import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.services.FilesIOService;

import java.io.File;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


public class FilesServiceTest {

	private static final int KIBIBYTE = 1024;
	private FilesIOService fs = new FilesIOService();

	@Test
	public void traverseDirTest() {
		List<FileDto> files = fs.listFilesInDir(new File("").getAbsoluteFile());
		FileDto pom = getPom(files);
		assertNotNull(pom);
		assertEquals(pom.getType(), FileDto.FileType.FILE);
		assertNotNull(pom.getSize());
		assertNotEquals(pom.getSize(), 0);
	}

	@Test
	public void calculateDirSize() {
		long size = fs.calculateDirSize(new File(".").getAbsoluteFile());
		assertNotNull(size);
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
