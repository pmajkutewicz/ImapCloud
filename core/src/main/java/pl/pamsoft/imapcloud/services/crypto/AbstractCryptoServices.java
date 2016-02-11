package pl.pamsoft.imapcloud.services.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public abstract class AbstractCryptoServices implements CryptoServices {

	@PostConstruct
	public void init() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		return getKeyPairGenerator().genKeyPair();
	}

	@Override
	public byte[] encode(byte[] publicKey, byte[] unencrypted) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = getKeyFactory();
		PublicKey pubKey = keyFactory.generatePublic(spec);
		Cipher cipher = getCipher();
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return cipher.doFinal(unencrypted);
	}

	@Override
	public byte[] decode(byte[] privateKey, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchProviderException {
		PKCS8EncodedKeySpec spec2 = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = getKeyFactory();
		PrivateKey privKey = keyFactory.generatePrivate(spec2);
		Cipher cipher = getCipher();
		cipher.init(Cipher.DECRYPT_MODE, privKey);
		return cipher.doFinal(encrypted);
	}

	abstract KeyFactory getKeyFactory() throws NoSuchAlgorithmException;

	abstract Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException;

	abstract KeyPairGenerator getKeyPairGenerator() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException;

}
