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

		byte[] encrypted = cs.encrypt(encryptingCipher, testData);
		byte[] decrypted = cs.decrypt(decryptingCipher, encrypted);

		assertArrayEquals(decrypted, testData);
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

		String encrypted = cs.encryptHex(encryptingCipher, testData);
		byte[] decrypted = cs.decryptHex(decryptingCipher, encrypted);

		assertArrayEquals(decrypted, testData);
	}

}
