package pl.pamsoft.imapcloud.services;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testng.annotations.DataProvider;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountServicesTest {

	private AccountServices services = new AccountServices();

	@BeforeAll
	public void init() {
		services.setCryptoService(new CryptoService());
	}

	@DataProvider
	Object[][] testData() {
		return new Object[][] {
			{"123"},
			{"1234567890"},
			{null}
		};
	}

	@Test(dataProvider = "testData")
	void shouldGenerate256bitsKey(String key) {
		CreateAccountRequest r = new CreateAccountRequest();
		r.setCryptoKey(key);
		String cryptoKey = services.getCryptoKey(r);
		assertEquals(32, ByteUtils.fromHexString(cryptoKey).length);
	}
}
