package pl.pamsoft.imapcloud.services.crypto;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Service
public class RSACryptoServices extends AbstractCryptoServices implements CryptoServices {

	private static final int KEYSIZE = 1024;

	@Override
	KeyPairGenerator getKeyPairGenerator() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(KEYSIZE);
		return kpg;
	}

	@Override
	KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
		return KeyFactory.getInstance("RSA");
	}

	@Override
	Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
		return Cipher.getInstance("RSA/ECB/PKCS1Padding");
	}
}
