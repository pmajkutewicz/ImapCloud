package pl.pamsoft.imapcloud.services;

import org.junit.Test;
import pl.pamsoft.imapcloud.services.crypto.AbstractCryptoServices;
import pl.pamsoft.imapcloud.services.crypto.CryptoServices;
import pl.pamsoft.imapcloud.services.crypto.ECCCryptoServices;
import pl.pamsoft.imapcloud.services.crypto.RSACryptoServices;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;

public class CryptoServicesTest {

	@Test
	public void shouldEncryptDecryptDataUsingRSA() throws NoSuchAlgorithmException, InvalidKeySpecException,
		BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
		String testData = "TestData";

		CryptoServices cs = new RSACryptoServices();
		KeyPair keyPair = cs.generateKeyPair();

		byte[] encoded = cs.encode(keyPair.getPublic().getEncoded(), testData.getBytes());
		byte[] unencoded = cs.decode(keyPair.getPrivate().getEncoded(), encoded);

		assertEquals(testData, new String(unencoded));
	}

	@Test
	public void shouldEncryptDecryptDataUsingECC() throws NoSuchAlgorithmException, InvalidKeySpecException,
		BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
		String testData = "TestData";

		AbstractCryptoServices cs = new ECCCryptoServices();
		cs.init();
		KeyPair keyPair = cs.generateKeyPair();

		byte[] encoded = cs.encode(keyPair.getPublic().getEncoded(), testData.getBytes());
		byte[] unencoded = cs.decode(keyPair.getPrivate().getEncoded(), encoded);

		assertEquals(testData, new String(unencoded));
	}

}
