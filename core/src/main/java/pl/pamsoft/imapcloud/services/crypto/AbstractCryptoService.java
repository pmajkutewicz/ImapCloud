package pl.pamsoft.imapcloud.services.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

public abstract class AbstractCryptoService implements CryptoService {

	@PostConstruct
	public void init() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	public byte[] encode(byte[] key, byte[] unencrypted) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = getCipher();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(unencrypted);
	}

	@Override
	public byte[] decode(byte[] key, byte[] encrypted) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = getCipher();
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(encrypted);
	}

	abstract Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException;

}
