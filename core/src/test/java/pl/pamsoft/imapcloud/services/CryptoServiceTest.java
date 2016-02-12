package pl.pamsoft.imapcloud.services;

import com.google.common.base.Stopwatch;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import pl.pamsoft.imapcloud.services.crypto.AESCryptoService;
import pl.pamsoft.imapcloud.services.crypto.CryptoService;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CryptoServiceTest {

	@Test
	public void list() throws NoSuchAlgorithmException {
		System.out.println(Cipher.getMaxAllowedKeyLength("AES"));
		for (Provider provider : Security.getProviders()) {
			System.out.println(provider.getName());
			for (String key : provider.stringPropertyNames())
				System.out.println("\t" + key + " -> " + provider.getProperty(key));
		}
	}

	@Test
	public void testLW() throws InvalidCipherTextException, BadPaddingException, ShortBufferException, IllegalBlockSizeException {
		byte[] saltData = new byte[1024 * 1024 * 10];
		byte[] exampleData = new byte[1024 * 1024 * 50];
		Random randomno = new Random();
		randomno.nextBytes(exampleData);
		randomno.nextBytes(saltData);

		ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
		ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
		Stopwatch stopwatch = Stopwatch.createStarted();
		AES_BC aes_bc = new AES_BC();
		aes_bc.encrypt(new ByteArrayInputStream(exampleData), encrypted);
		aes_bc.decrypt(new ByteArrayInputStream(encrypted.toByteArray()), outBaos);
		System.out.println("Time: " + stopwatch);
		assertArrayEquals(exampleData, outBaos.toByteArray());
	}

	@Test
	public void aa() throws Exception {
		byte[] saltData = new byte[1024 * 1024 * 10];
		byte[] exampleData = new byte[1024 * 1024 * 50];
		Random randomno = new Random();
		randomno.nextBytes(exampleData);
		randomno.nextBytes(saltData);

		Stopwatch stopwatch = Stopwatch.createStarted();
		char[] password = "password".toCharArray();
		int iterationCount = 100000;
		byte[] encrypt = encrypt(exampleData, password, saltData, iterationCount);
		byte[] out = decrypt(encrypt, password, saltData, iterationCount);

		System.out.println("Time: " + stopwatch);

		assertArrayEquals(exampleData, out);
	}

	private byte[] decryptWithLWCrypto(byte[] cipher, char[] password, byte[] salt, final int iterationCount)
		throws Exception {
		PKCS12ParametersGenerator pGen = new PKCS12ParametersGenerator(new SHA256Digest());
		final byte[] pkcs12PasswordBytes = PBEParametersGenerator.PKCS12PasswordToBytes(password);
		pGen.init(pkcs12PasswordBytes, salt, iterationCount);


		CBCBlockCipher aesCBC = new CBCBlockCipher(new AESEngine());
		ParametersWithIV aesCBCParams = (ParametersWithIV) pGen.generateDerivedParameters(256, 128);
		aesCBC.init(false, aesCBCParams);
		PaddedBufferedBlockCipher aesCipher = new PaddedBufferedBlockCipher(aesCBC, new PKCS7Padding());
		byte[] plainTemp = new byte[aesCipher.getOutputSize(cipher.length)];
		int offset = aesCipher.processBytes(cipher, 0, cipher.length, plainTemp, 0);
		int last = aesCipher.doFinal(plainTemp, offset);
		final byte[] plain = new byte[offset + last];
		System.arraycopy(plainTemp, 0, plain, 0, plain.length);
		return plain;
	}

	private static byte[] decrypt(byte[] toEncrypt, char[] password, byte[] salt, final int iterationCount) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Security.addProvider(new BouncyCastleProvider());

		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, iterationCount);

		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

		Cipher encryptionCipher = Cipher.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		encryptionCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);

		return encryptionCipher.doFinal(toEncrypt);
	}

	private static byte[] encrypt(byte[] toEncrypt, char[] password, byte[] salt, final int iterationCount) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Security.addProvider(new BouncyCastleProvider());

		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, iterationCount);

		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

		Cipher encryptionCipher = Cipher.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
		encryptionCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

		return encryptionCipher.doFinal(toEncrypt);
	}

	@Test
	public void shouldEncryptDecryptDataUsingAES() throws NoSuchAlgorithmException, InvalidKeySpecException,
		BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
		String testData = "TestData";

		CryptoService cs = new AESCryptoService();
		byte[] secretKey = cs.getkey();

		byte[] encoded = cs.encode(secretKey, testData.getBytes());
		byte[] unencoded = cs.decode(secretKey, encoded);

		assertEquals(testData, new String(unencoded));
	}

}
