package pl.pamsoft.imapcloud.exceptions;

import java.nio.file.FileSystemException;

public class ChunkAlreadyExistException extends FileSystemException {
	public ChunkAlreadyExistException(String message) {
		super(message);
	}
}
