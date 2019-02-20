package pl.pamsoft.imapcloud.services.recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.services.FilesIOService;
import pl.pamsoft.imapcloud.services.containers.RecoveryChunkContainer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class RecoveredFileChunksFileWriterTest {

	private static final String RAM_VIRTUAL = "ram://virtual";
	private RecoveredFileChunksFileWriter recoveredFileChunksFileWriter;

	private FilesIOService filesIOService = mock(FilesIOService.class);
	private FileSystemManager fsManager;

	@BeforeEach
	void init() throws FileSystemException {
		recoveredFileChunksFileWriter = new RecoveredFileChunksFileWriter(filesIOService, ".");
		fsManager = VFS.getManager();
		fsManager.createVirtualFileSystem(RAM_VIRTUAL);
		reset(filesIOService);
	}

	@Test
	void shouldSaveJsonToFile() throws IOException {
		String random = RandomStringUtils.randomAlphanumeric(10);
		String file = RAM_VIRTUAL + "/" + random + ".ic";
		OutputStream os = create(file).getContent().getOutputStream();
		when(filesIOService.getOutputStream(any())).thenReturn(os);
		when(filesIOService.unPack(any())).thenCallRealMethod();
		doCallRealMethod().when(filesIOService).packToFile(any(), anyString(), anyString());
		RecoveryChunkContainer dummyData = create();

		RecoveryChunkContainer result = recoveredFileChunksFileWriter.apply(dummyData);

		InputStream is = fsManager.resolveFile(file).getContent().getInputStream();
		String jsonAsString = filesIOService.unPack(is);

		RecoveryChunkContainer recoveryChunkContainer = new ObjectMapper().readValue(jsonAsString, RecoveryChunkContainer.class);
		assertEquals(dummyData, result);
		assertEquals(dummyData.getTaskId(), recoveryChunkContainer.getTaskId());
	}

	@Test
	void shouldReturnEmptyContainerWhenErrorOccured() throws IOException {
		when(filesIOService.getOutputStream(any())).thenThrow(new IOException("success"));
		RecoveryChunkContainer dummyData = create();

		RecoveryChunkContainer result = recoveredFileChunksFileWriter.apply(dummyData);

		assertEquals(RecoveryChunkContainer.EMPTY, result);
	}

	private FileObject create(String file) throws FileSystemException {
		FileObject fileObject = fsManager.resolveFile(file);
		fileObject.createFile();
		return fileObject;
	}

	private RecoveryChunkContainer create() {
		Account a = new Account();
		a.setLogin("test");
		a.setPassword("test");
		Map<String, File> fileMap = new HashMap<>();
		Map<String, List<FileChunk>> chunkMap = new HashMap<>();
		RecoveryChunkContainer rcc = new RecoveryChunkContainer("testId", a);
		return RecoveryChunkContainer.addRecoveredFilesData(rcc, fileMap, chunkMap);
	}
}
