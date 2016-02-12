package pl.pamsoft.imapcloud.services.crypto;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

@Service
public class AESCryptoService extends AbstractCryptoService implements CryptoService {

	private static final int KEYSIZE = 128;

	@Override
	public byte[] getkey() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(KEYSIZE);
		return kgen.generateKey().getEncoded();
	}

	@Override
	Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
		return Cipher.getInstance("AES");
	}
}
