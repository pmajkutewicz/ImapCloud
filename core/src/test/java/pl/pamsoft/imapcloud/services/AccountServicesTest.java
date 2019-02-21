package pl.pamsoft.imapcloud.services;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServicesTest {

	private AccountServices services = new AccountServices();

	static Stream<Arguments> testData() {
		return Stream.of(of("123"), of("1234567890"), null);
	}

	@BeforeAll
	void init() {
		services.setCryptoService(new CryptoService());
	}

	@ParameterizedTest
	@MethodSource("testData")
	void shouldGenerate256bitsKey(String key) {
		CreateAccountRequest r = new CreateAccountRequest();
		r.setCryptoKey(key);
		String cryptoKey = services.getCryptoKey(r);
		assertEquals(32, ByteUtils.fromHexString(cryptoKey).length);
	}
}
