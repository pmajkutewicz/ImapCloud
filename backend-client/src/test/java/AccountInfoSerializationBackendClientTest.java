import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.rest.json.MapperHolder;

import java.io.IOException;

import static com.google.common.collect.ImmutableMap.of;

public class AccountInfoSerializationBackendClientTest {

	@Test
	public void shouldSerialize() throws JsonProcessingException {
		ObjectMapper mapper = MapperHolder.OBJECT_MAPPER;
		AccountInfo accountInfo = new AccountInfo("imap", "test", 1, 3, 2, of("test1", "testVal"));
		String expected = "{\"type\":\"imap\",\"host\":\"test\",\"accountSizeMB\":3,\"maxFileSizeMB\":2,\"maxConcurrentConnections\":1,\"additionalProperties\":{\"test1\":\"testVal\"}}";;

		String result = mapper.writeValueAsString(accountInfo);

		Assert.assertEquals(result, expected);
	}

	@Test
	public void shouldDeserialize() throws IOException {
		ObjectMapper mapper = MapperHolder.OBJECT_MAPPER;
		String example = "{\"type\":\"imap\",\"host\":\"test\",\"accountSizeMB\":3,\"maxFileSizeMB\":2,\"maxConcurrentConnections\":1,\"additionalProperties\":{\"test1\":\"testVal\"}}";

		AccountInfo result = mapper.readValue(example, AccountInfo.class);

		Assert.assertEquals(result.getAccountSizeMB().intValue(), 3);
		Assert.assertEquals(result.getType(), "imap");
		Assert.assertEquals(result.getAdditionalProperties().size(), 1);
		Assert.assertEquals(result.getAdditionalProperties().get("test1"), "testVal");
	}
}
