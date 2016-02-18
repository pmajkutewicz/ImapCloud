package pl.pamsoft.imapcloud.services;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

@Service
public class CryptoService {

	private static final boolean ENCRYPT_MODE = true;
	private static final boolean DECRYPT_MODE = false;
	private static final int COPY_BUFFER_SIZE = 1024 * 1024;
	private static final int KEYSIZE_IN_BYTES = 256;
	private static final int TO_BITS = 8;

	public byte[] generateKey() throws NoSuchProviderException, NoSuchAlgorithmException {
		byte[] key = new byte[KEYSIZE_IN_BYTES / TO_BITS];
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// calling nextBytes twice: https://www.cigital.com/blog/proper-use-of-javas-securerandom
		random.nextBytes(key);
		random.nextBytes(key);
		return key;
	}

	public PaddedBufferedBlockCipher getEncryptingCipher(byte[] key) {
		return getCipher(key, ENCRYPT_MODE);
	}

	public PaddedBufferedBlockCipher getDecryptingCipher(byte[] key) {
		return getCipher(key, DECRYPT_MODE);
	}

	private PaddedBufferedBlockCipher getCipher(byte[] key, boolean mode) {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine());
		cipher.init(mode, new KeyParameter(key));
		return cipher;
	}

	public byte[] decrypt(PaddedBufferedBlockCipher decryptCipher, byte[] in) throws IOException, InvalidCipherTextException {
		return process(decryptCipher, in);
	}

	public byte[] encrypt(PaddedBufferedBlockCipher encryptCipher, byte[] in) throws IOException, InvalidCipherTextException {
		return process(encryptCipher, in);
	}

	private byte[] process(PaddedBufferedBlockCipher cipher, byte[] in) throws IOException, InvalidCipherTextException {
		byte[] buf = new byte[COPY_BUFFER_SIZE];
		byte[] obuf = new byte[COPY_BUFFER_SIZE];
		int noBytesRead = 0;
		int noBytesProcessed = 0;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream is = new ByteArrayInputStream(in);
		while ((noBytesRead = is.read(buf)) >= 0) {
			noBytesProcessed = cipher.processBytes(buf, 0, noBytesRead, obuf, 0);
			out.write(obuf, 0, noBytesProcessed);
		}
		noBytesProcessed = cipher.doFinal(obuf, 0);
		out.write(obuf, 0, noBytesProcessed);
		out.flush();
		return out.toByteArray();
	}

	public String rot13(String input) {
		StringBuilder sb = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c >= 'a' && c <= 'm') c += 13;
			else if (c >= 'A' && c <= 'M') c += 13;
			else if (c >= 'n' && c <= 'z') c -= 13;
			else if (c >= 'N' && c <= 'Z') c -= 13;
			sb.append(c);
		}
		return sb.toString();
	}
}
