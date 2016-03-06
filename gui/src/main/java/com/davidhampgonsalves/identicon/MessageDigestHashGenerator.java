package com.davidhampgonsalves.identicon;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MessageDigestHashGenerator implements HashGeneratorInterface {
	private static final String CHARSET = StandardCharsets.UTF_8.toString();
	private MessageDigest messageDigest;

	public MessageDigestHashGenerator(String algorithim) {
		try {
			messageDigest = MessageDigest.getInstance(algorithim);
		} catch (Exception e) {
			System.err.println("Error setting algorithim: " + algorithim);
		}
	}

	public byte[] generate(String input) {
		try {
			return messageDigest.digest(input.getBytes(CHARSET));
		} catch (UnsupportedEncodingException e) {
			return new byte[0];
		}
	}
}
