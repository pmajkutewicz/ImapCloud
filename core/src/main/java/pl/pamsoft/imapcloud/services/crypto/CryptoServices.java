package pl.pamsoft.imapcloud.services.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public interface CryptoServices {
	KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException;

	byte[] encode(byte[] publicKey, byte[] unencrypted) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException;

	byte[] decode(byte[] privateKey, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchProviderException, InvalidAlgorithmParameterException;
}
