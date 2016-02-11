package pl.pamsoft.imapcloud.services.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;

/**
 * This can be done "wrong".
 * Findbug: CIPHER_INTEGRITY
 * @see http://bouncy-castle.1462172.n4.nabble.com/ECC-with-ECIES-Encrypt-Decrypt-Questions-td4656750.html
 */
public class ECCCryptoServices extends AbstractCryptoServices implements CryptoServices {

	@Override
	KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
		return KeyFactory.getInstance("ECDH");
	}

	@Override
	Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
		return Cipher.getInstance("ECIES", BouncyCastleProvider.PROVIDER_NAME);
	}

	@Override
	KeyPairGenerator getKeyPairGenerator() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
		kpg.initialize(new ECGenParameterSpec("secp256r1"));
		return kpg;
	}

}
