package pl.pamsoft.imapcloud.services.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface CryptoService {
	byte[] getkey() throws NoSuchAlgorithmException;

	byte[] encode(byte[] key, byte[] unencrypted) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException;

	byte[] decode(byte[] key, byte[] encrypted) throws InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException;
}
