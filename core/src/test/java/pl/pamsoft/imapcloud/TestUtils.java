package pl.pamsoft.imapcloud;

import java.util.Random;

public class TestUtils {

	public static byte[] getRandomBytes(int size) {
		byte[] in = new byte[size];
		new Random().nextBytes(in);
		return in;
	}
}
