import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.rest.json.MapperHolder;

import java.io.IOException;

import static com.google.common.collect.ImmutableMap.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountInfoSerializationBackendClientTest {

	@Test
	void shouldSerialize() throws JsonProcessingException {
		ObjectMapper mapper = MapperHolder.OBJECT_MAPPER;
		AccountInfo accountInfo = new AccountInfo("imap", "test", 1, 3, 2, of("test1", "testVal"));
		String expected = "{\"type\":\"imap\",\"host\":\"test\",\"accountSizeMB\":3,\"maxFileSizeMB\":2,\"maxConcurrentConnections\":1,\"additionalProperties\":{\"test1\":\"testVal\"}}";

		String result = mapper.writeValueAsString(accountInfo);

		assertEquals(expected, result);
	}

	@Test
	void shouldDeserialize() throws IOException {
		ObjectMapper mapper = MapperHolder.OBJECT_MAPPER;
		String example = "{\"type\":\"imap\",\"host\":\"test\",\"accountSizeMB\":3,\"maxFileSizeMB\":2,\"maxConcurrentConnections\":1,\"additionalProperties\":{\"test1\":\"testVal\"}}";

		AccountInfo result = mapper.readValue(example, AccountInfo.class);

		assertEquals(3, result.getAccountSizeMB().intValue());
		assertEquals("imap", result.getType());
		assertEquals(1, result.getAdditionalProperties().size());
		assertEquals("testVal", result.getAdditionalProperties().get("test1"));
	}
}
