package pl.pamsoft.imapcloud.services;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;

import static org.testng.Assert.assertEquals;

public class AccountServicesTest {

	private AccountServices services = new AccountServices();

	@BeforeClass
	public void init() {
		services.setCryptoService(new CryptoService());
	}

	@DataProvider
	public Object[][] testData() {
		return new Object[][] {
			{"123"},
			{"1234567890"},
			{null}
		};
	}

	@Test(dataProvider = "testData")
	public void shouldGenerate256bitsKey(String key) {
		CreateAccountRequest r = new CreateAccountRequest();
		r.setCryptoKey(key);
		String cryptoKey = services.getCryptoKey(r);
		assertEquals(ByteUtils.fromHexString(cryptoKey).length, 32);
	}
}
