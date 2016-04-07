package pl.pamsoft.imapcloud.exceptions;

import java.io.IOException;

public class AccountAlreadyExistException extends IOException {
	public AccountAlreadyExistException(String message) {
		super(message);
	}
}
