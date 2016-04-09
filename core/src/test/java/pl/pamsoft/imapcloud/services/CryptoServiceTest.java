package pl.pamsoft.imapcloud.services;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

public class CryptoServiceTest {

	@Test
	public void shouldEncryptDecryptDataUsingAES() throws IOException, InvalidCipherTextException {
		byte[] key = new byte[256 / 8];
		byte[] testData = new byte[50 * 1024 * 1024];
		Random random = new Random();
		random.nextBytes(key);
		random.nextBytes(testData);

		CryptoService cs = new CryptoService();
		PaddedBufferedBlockCipher decryptingCipher = cs.getDecryptingCipher(key);
		PaddedBufferedBlockCipher encryptingCipher = cs.getEncryptingCipher(key);

		byte[] encoded = cs.encrypt(encryptingCipher, testData);
		byte[] unencoded = cs.decrypt(decryptingCipher, encoded);

		assertArrayEquals(unencoded, testData);
	}

	@Test
	public void shouldEncryptDecryptWithHex() throws IOException, InvalidCipherTextException {
		byte[] key = new byte[256 / 8];
		byte[] testData = new byte[50 * 1024 * 1024];
		Random random = new Random();
		random.nextBytes(key);
		random.nextBytes(testData);

		CryptoService cs = new CryptoService();
		PaddedBufferedBlockCipher decryptingCipher = cs.getDecryptingCipher(key);
		PaddedBufferedBlockCipher encryptingCipher = cs.getEncryptingCipher(key);

		String encoded = cs.encryptHex(encryptingCipher, testData);
		byte[] unencoded = cs.decryptHex(decryptingCipher, encoded);

		assertArrayEquals(unencoded, testData);
	}

}
