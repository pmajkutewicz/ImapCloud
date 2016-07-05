package pl.pamsoft.imapcloud.imap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.json.JSONObject;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class ChunkRecoveryTest {

	private static final int MAX_CHUNK_SIZE = 1024 * 1024 * 10; // 10MB
	private ChunkRecovery chunkRecovery;

	@Mock
	private GenericObjectPool<Store> pool;
	@Mock
	private Statistics statistics;
	@Mock
	private PerformanceDataService performanceDataService;

	@Mock
	private Store store;
	@Mock
	private Folder mailICFolder;

	private Random random = new Random();

	@BeforeMethod
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		JSONObject jsonObject = loadData();
		Folder[] folderList = createFolderList(jsonObject);

		when(pool.borrowObject()).thenReturn(store);
		when(mailICFolder.list()).thenReturn(folderList);
		when(store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME)).thenReturn(mailICFolder);

		this.chunkRecovery = new ChunkRecovery(pool, statistics, performanceDataService);
	}

	@Test
	public void shouldRecoverMailbox() {
		RecoveryChunkContainer result = chunkRecovery.apply(RecoveryChunkContainer.EMPTY);
		assertEquals(result.getFileMap().size(), 4);
		assertEquals(result.getFileChunkMap().get("9be83237-0d72-43e2-944b-de81715c93f2").size(), 830);
		assertEquals(result.getFileChunkMap().get("d1f93ec9-2067-4c98-a4e0-2aeb6535fa81").size(), 830);
		assertEquals(result.getFileChunkMap().get("09f82836-b89f-4606-86e8-7f3d54938aca").size(), 610);
		assertEquals(result.getFileChunkMap().get("83f6ffaf-d275-49c4-b23a-f8f12f4a8774").size(), 639);
	}

	@Test
	public void shouldInvalidateConnectionOnError() throws Exception {
		reset(store);
		when(store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME)).thenThrow(new MessagingException("success"));

		RecoveryChunkContainer result = chunkRecovery.apply(RecoveryChunkContainer.EMPTY);

		assertEquals(RecoveryChunkContainer.EMPTY, result);
		verify(pool).invalidateObject(store);
	}

	@Test
	public void inCaseOfInvalidationErrorEmptyResultShouldBeReturned() throws Exception {
		reset(store);
		when(store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME)).thenThrow(new MessagingException("success"));
		doThrow(new MessagingException("success")).when(pool).invalidateObject(store);

		RecoveryChunkContainer result = chunkRecovery.apply(RecoveryChunkContainer.EMPTY);

		assertEquals(RecoveryChunkContainer.EMPTY, result);
		verify(pool).invalidateObject(store);
	}

	private Folder[] createFolderList(JSONObject jsonObject) throws MessagingException {
		List<Folder> folders = new ArrayList<>();
		for (Object item : jsonObject.keySet()) {
			String key = (String) item;
			JSONObject o = (JSONObject) jsonObject.get(key);
			Message[] messages = createMessages(o);

			Folder f = mock(Folder.class);
			when(f.getFullName()).thenReturn(key);
			when(f.getMessageCount()).thenReturn(o.length());
			when(f.getMessages()).thenReturn(messages);
			folders.add(f);
		}

		return folders.toArray(new Folder[folders.size()]);
	}

	private Message[] createMessages(JSONObject jsonObject) throws MessagingException {
		List<Message> messages = new ArrayList<>();
		for (Object item : jsonObject.keySet()) {
			String key = (String) item;
			JSONObject o = (JSONObject) jsonObject.get(key);
			List<Header> headers = createHeadersMap(o);

			Message m = mock(Message.class);
			when(m.getAllHeaders()).thenReturn(Collections.enumeration(headers));
			when(m.getSize()).thenReturn(random.nextInt(MAX_CHUNK_SIZE));
			messages.add(m);
		}
		return messages.toArray(new Message[messages.size()]);
	}

	private List<Header> createHeadersMap(JSONObject jsonObject) {
		List<Header> result = new ArrayList<>();
		for (Object item : jsonObject.keySet()) {
			String key = (String) item;
			String value = (String) jsonObject.get(key);
			result.add(new Header(key, value));
		}
		result.add(new Header("size", String.valueOf(random.nextInt(1024))));
		return result;
	}

	private JSONObject loadData() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("messages.json");
		String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
		return new JSONObject(jsonTxt);
	}
}