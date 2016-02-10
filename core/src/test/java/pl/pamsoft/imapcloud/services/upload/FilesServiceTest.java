package pl.pamsoft.imapcloud.services.upload;

import org.junit.Test;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.services.upload.FilesService;

import java.io.File;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FilesServiceTest {

	private static final int MEBIBYTE = 1024 * 1024;
	private FilesService fs = new FilesService();

	@Test
	public void traverseDirTest() {
		List<FileDto> files = fs.listFilesInDir(new File("").getAbsoluteFile());
		FileDto pom = getPom(files);
		assertNotNull(pom);
		assertThat(pom.getType(), is(FileDto.Type.FILE));
		assertThat(pom.getSize(), not(nullValue()));
		assertThat(pom.getSize(), not(0));
	}

	@Test
	public void calculateDirSize() {
		long size = fs.calculateDirSize(new File(".").getAbsoluteFile());
		assertThat(size, not(nullValue()));
		assertThat(size, not(0));
		assertThat(size > MEBIBYTE, is(true));
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
