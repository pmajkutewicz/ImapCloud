package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.imap.IMAPConnectionFactory;
import pl.pamsoft.imapcloud.services.upload.ChunkEncoder;
import pl.pamsoft.imapcloud.services.upload.ChunkHasher;
import pl.pamsoft.imapcloud.services.upload.DirectoryProcessor;
import pl.pamsoft.imapcloud.services.upload.FileSplitter;
import pl.pamsoft.imapcloud.services.upload.FilesIOService;

import javax.mail.Store;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
public class UploadService {

	private static final int MAX_CONNECTIONS_TO_IMAP_SERVER = 4;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private FileServices fileServices;

	@Autowired
	private CryptoService cryptoService;

	public void upload(AccountDto selectedAccount, List<FileDto> selectedFiles, boolean chunkEncodingEnabled) {
		try {
			MessageDigest instance = MessageDigest.getInstance("SHA-512");
			Account account = accountRepository.getById(selectedAccount.getId());
			Function<FileDto, Stream<FileDto>> parseDirectories = new DirectoryProcessor(filesIOService);
			Predicate<FileDto> removeFilesWithSize0 = fileDto -> fileDto.getSize() > 0;
			Consumer<FileDto> storeFile = fileDto -> fileServices.save(fileDto, account);
			Function<FileDto, Stream<UploadChunkContainer>> splitFileIntoChunks = new FileSplitter(account.getAttachmentSizeMB(), 2);
			Function<UploadChunkContainer, UploadChunkContainer> hashGenerator = new ChunkHasher(instance);
			Function<UploadChunkContainer, UploadChunkContainer> chunkEncoder = new ChunkEncoder(cryptoService, account.getCryptoKey());
			//Consumer<UploadChunkContainer> saveOnIMAPServer = new ChunkSaver(createConnectionPool(account));

			selectedFiles.stream()
				.flatMap(parseDirectories)
				.filter(removeFilesWithSize0)
				.peek(storeFile)
				.flatMap(splitFileIntoChunks)
				.map(hashGenerator)
				.map(chunkEncoder)
				.forEach(System.out::println);

			System.out.println(account);
//		new FileChunkIterator(selectedAccount.)
//		selectedFiles.stream().
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}


	private GenericObjectPool<Store> createConnectionPool(Account account) {
		IMAPConnectionFactory connectionFactory = new IMAPConnectionFactory(account.getLogin(), account.getPassword(), account.getImapServerAddress());
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(MAX_CONNECTIONS_TO_IMAP_SERVER);
		return new GenericObjectPool<>(connectionFactory, config);
	}
}
