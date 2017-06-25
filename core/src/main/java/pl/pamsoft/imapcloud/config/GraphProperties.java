package pl.pamsoft.imapcloud.config;

public class GraphProperties {

	public static final String ACCOUNT_EMAIL = "email";
	public static final String ACCOUNT_LOGIN = "login";
	public static final String ACCOUNT_PASSWORD = "password";
	public static final String ACCOUNT_HOST = "host";
	public static final String ACCOUNT_MAX_CONCURRENT_CONNECTIONS = "maxConcurrentConnections";
	public static final String ACCOUNT_ACCOUNT_SIZE_MB = "accountSizeMB";
	public static final String ACCOUNT_ATTACHMENT_SIZE_MB = "maxFileAccountSizeMB";
	public static final String ACCOUNT_CRYPTO_KEY = "cryptoKey";

	public static final String FILE_UNIQUE_ID = "fileUniqueId";
	public static final String FILE_HASH = "fileHash";
	public static final String FILE_NAME = "name";
	public static final String FILE_ABSOLUTE_PATH = "absolutePath";
	public static final String FILE_SIZE = "size";
	public static final String FILE_COMPLETED = "completed";
	public static final String FILE_EDGE_ACCOUNT = "is_owned_by";

	public static final String FILE_CHUNK_UNIQUE_ID = "fileChunkUniqueId";
	public static final String FILE_CHUNK_NUMBER = "chunkNumber";
	public static final String FILE_CHUNK_HASH = "chunkHash";
	public static final String FILE_CHUNK_SIZE = "size";
	public static final String FILE_CHUNK_MESSAGE_ID = "messageId";
	public static final String FILE_CHUNK_LAST_CHUNK = "lastChunk";
	public static final String FILE_CHUNK_LAST_VERIFIED_AT = "lastVerifiedAt";
	public static final String FILE_CHUNK_EXISTS = "chunkExists";
	public static final String FILE_CHUNK_EDGE_FILE = "is_part_of";
}
