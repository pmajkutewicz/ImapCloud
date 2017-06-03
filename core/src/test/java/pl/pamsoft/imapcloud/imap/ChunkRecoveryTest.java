package pl.pamsoft.imapcloud.imap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.monitoring.MonitoringHelper;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class ChunkRecoveryTest {

	private static final int MAX_CHUNK_SIZE = 1024 * 1024 * 10; // 10MB
	private ChunkRecovery chunkRecovery;

	@Mock
	private GenericObjectPool<Store> pool;
	@Mock
	private MonitoringHelper monitoringHelper;

	@Mock
	private Store store;
	@Mock
	private Folder mailICFolder;

	private Random random = new SecureRandom();

	@BeforeMethod
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
		JsonNode jsonObject = loadData();
		Folder[] folderList = createFolderList(jsonObject);

		when(pool.borrowObject()).thenReturn(store);
		when(mailICFolder.list()).thenReturn(folderList);
		when(store.getFolder(IMAPUtils.IMAP_CLOUD_FOLDER_NAME)).thenReturn(mailICFolder);

		this.chunkRecovery = new ChunkRecovery(pool, monitoringHelper);
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

	private Folder[] createFolderList(JsonNode node) throws MessagingException {
		List<Folder> folders = new ArrayList<>();

		Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
		while (iter.hasNext()) {
			Map.Entry<String, JsonNode> next = iter.next();
			String key = next.getKey();
			JsonNode o = next.getValue();
			Message[] messages = createMessages(o);

			Folder f = mock(Folder.class);
			when(f.getFullName()).thenReturn(key);
			when(f.getMessageCount()).thenReturn(o.size());
			when(f.getMessages()).thenReturn(messages);
			folders.add(f);
		}

		return folders.toArray(new Folder[folders.size()]);
	}

	private Message[] createMessages(JsonNode node) throws MessagingException {
		List<Message> messages = new ArrayList<>();
		Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
		while (iter.hasNext()) {
			Map.Entry<String, JsonNode> next = iter.next();
			JsonNode o = next.getValue();
			List<Header> headers = createHeadersMap(o);

			Message m = mock(Message.class);
			when(m.getAllHeaders()).thenReturn(Collections.enumeration(headers));
			ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
			when(m.getHeader(headerCaptor.capture())).thenAnswer(new Answer<String[]>() {
				@Override
				public String[] answer(InvocationOnMock invocationOnMock) throws Throwable {
					return headers.stream()
						.filter(h -> h.getName().equals(headerCaptor.getValue()))
						.map(Header::getValue).collect(toList()).toArray(new String[1]);
				}
			});
			when(m.getSize()).thenReturn(random.nextInt(MAX_CHUNK_SIZE));
			messages.add(m);
		}
		return messages.toArray(new Message[messages.size()]);
	}

	private List<Header> createHeadersMap(JsonNode node) {
		List<Header> result = new ArrayList<>();

		Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
		while (iter.hasNext()) {
			Map.Entry<String, JsonNode> next = iter.next();
			String key = next.getKey();
			String value = next.getValue().asText();
			result.add(new Header(key, value));
		}

		result.add(new Header("size", String.valueOf(random.nextInt(1024))));
		return result;
	}

	private JsonNode loadData() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("messages.json");
		String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
		return new ObjectMapper().readTree(jsonTxt);
	}
}
