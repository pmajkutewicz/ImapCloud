package pl.pamsoft.imapcloud.services;

import com.google.common.io.BaseEncoding;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Random;

@Service
public class CryptoService {

	private static final Logger LOG = LoggerFactory.getLogger(CryptoService.class);

	private static final boolean ENCRYPT_MODE = true;
	private static final boolean DECRYPT_MODE = false;
	private static final int COPY_BUFFER_SIZE = 1024 * 1024;
	private static final int KEYSIZE_IN_BYTES = 256;
	private static final int TO_BITS = 8;
	private static final int ROT_13 = 13;

	public byte[] generateKey() throws NoSuchProviderException, NoSuchAlgorithmException {
		byte[] key = new byte[KEYSIZE_IN_BYTES / TO_BITS];
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// calling nextBytes twice: https://www.cigital.com/blog/proper-use-of-javas-securerandom
		random.nextBytes(key);
		random.nextBytes(key);
		return key;
	}

	public byte[] calcSha256(String passphrase) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return digest.digest(passphrase.getBytes(StandardCharsets.UTF_8));
	}

	@SuppressFBWarnings("PREDICTABLE_RANDOM")
	public byte[] generateWeakKey() {
		Random random = new Random();
		final byte[] buffer = new byte[KEYSIZE_IN_BYTES];
		random.nextBytes(buffer);
		return buffer;
	}

	public PaddedBufferedBlockCipher getEncryptingCipher(byte[] key) {
		return getCipher(key, ENCRYPT_MODE);
	}

	public PaddedBufferedBlockCipher getDecryptingCipher(byte[] key) {
		return getCipher(key, DECRYPT_MODE);
	}

	private PaddedBufferedBlockCipher getCipher(byte[] key, boolean mode) {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine());
		KeyParameter cipherParameters = new KeyParameter(key);
		try {
			cipher.init(mode, cipherParameters);
		} catch (Exception e) {
			LOG.error("Can't init cipher", e);
		}
		return cipher;
	}

	public byte[] decrypt(PaddedBufferedBlockCipher decryptCipher, byte[] in) throws IOException, InvalidCipherTextException {
		return process(decryptCipher, in);
	}

	public byte[] encrypt(PaddedBufferedBlockCipher encryptCipher, byte[] in) throws IOException, InvalidCipherTextException {
		return process(encryptCipher, in);
	}

	public byte[] decryptHex(PaddedBufferedBlockCipher decryptCipher, String hexString) throws IOException, InvalidCipherTextException {
		byte[] toDecode = BaseEncoding.base16().upperCase().decode(hexString);
		return decrypt(decryptCipher, toDecode);
	}

	public String encryptHex(PaddedBufferedBlockCipher encryptCipher, byte[] in) throws IOException, InvalidCipherTextException {
		byte[] encrypted = encrypt(encryptCipher, in);
		return BaseEncoding.base16().upperCase().encode(encrypted);
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
			if (c >= 'a' && c <= 'm') {
				c += ROT_13;
			} else if (c >= 'A' && c <= 'M') {
				c += ROT_13;
			} else if (c >= 'n' && c <= 'z') {
				c -= ROT_13;
			} else if (c >= 'N' && c <= 'Z') {
				c -= ROT_13;
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
