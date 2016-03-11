package pl.pamsoft.imapcloud.services;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.imap.ChunkSaver;
import pl.pamsoft.imapcloud.imap.IMAPConnectionFactory;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.upload.ChunkEncoder;
import pl.pamsoft.imapcloud.services.upload.ChunkHasher;
import pl.pamsoft.imapcloud.services.upload.DirectoryProcessor;
import pl.pamsoft.imapcloud.services.upload.DirectorySizeCalculator;
import pl.pamsoft.imapcloud.services.upload.FileChunkStorer;
import pl.pamsoft.imapcloud.services.upload.FileHasher;
import pl.pamsoft.imapcloud.services.upload.FileSplitter;
import pl.pamsoft.imapcloud.services.upload.FileStorer;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;
import pl.pamsoft.imapcloud.services.websocket.TasksProgressService;
import pl.pamsoft.imapcloud.websocket.TaskProgressEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.Store;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
public class UploadService {

	private static final int MAX_TASKS = 10;
	private static final int FIVETEEN = 15;

	private ExecutorService executor = Executors.newFixedThreadPool(MAX_TASKS, new ThreadFactoryBuilder().setNameFormat("UploadTask-%d").setDaemon(false).build());
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	private Map<String, Future<?>> taskMap = new ConcurrentHashMap<>();
	private Map<String, TaskProgressEvent> taskProgressMap = new ConcurrentHashMap<>();
	private Map<String, GenericObjectPool<Store>> accountPoolMap = new ConcurrentHashMap<>();

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FilesIOService filesIOService;

	@Autowired
	private FileServices fileServices;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private Statistics statistics;

	@Autowired
	private PerformanceDataService performanceDataService;

	@Autowired
	private TasksProgressService tasksProgressService;

	private Callable<Void> cleanUpTask = () -> {
		for (Map.Entry<String, Future<?>> taskEntry : taskMap.entrySet()) {
			if (taskEntry.getValue().isDone()) {
				taskProgressMap.remove(taskEntry.getKey());
				taskMap.remove(taskEntry.getKey());
			}
		}
		return null;
	};

	@PostConstruct
	void init() {
		scheduledExecutorService.schedule(cleanUpTask, FIVETEEN, TimeUnit.MINUTES);
	}

	@PreDestroy
	void destroy() {
		executor.shutdown();
		scheduledExecutorService.shutdown();
	}

	@SuppressFBWarnings("STT_TOSTRING_STORED_IN_FIELD")
	public boolean upload(AccountDto selectedAccount, List<FileDto> selectedFiles, boolean chunkEncodingEnabled) throws RejectedExecutionException {
		final String taskId = UUID.randomUUID().toString();
		Future<?> task = executor.submit(() -> {
			Thread.currentThread().setName("UploadTask-" + taskId);
			try {
				final MessageDigest instance = MessageDigest.getInstance("SHA-512");
				final Account account = accountRepository.getById(selectedAccount.getId());
				final Long bytesToProcess = new DirectorySizeCalculator(filesIOService, statistics, performanceDataService).apply(selectedFiles);
				taskProgressMap.put(taskId, new TaskProgressEvent(taskId, bytesToProcess));

				Predicate<UploadChunkContainer> filterEmptyUcc = ucc -> UploadChunkContainer.EMPTY != ucc;
				Consumer<UploadChunkContainer> updateProgress = ucc -> taskProgressMap.get(ucc.getTaskId())
					.process(ucc.getChunkSize(), ucc.getFileDto().getAbsolutePath(), ucc.getCurrentFileChunkCumulativeSize(), ucc.getFileDto().getSize());
				Consumer<UploadChunkContainer> markFileProcessed = ucc -> taskProgressMap.get(ucc.getTaskId())
					.markFileProcessed(ucc.getFileDto().getAbsolutePath(), ucc.getFileDto().getSize());

				Consumer<UploadChunkContainer> broadcastTaskProgress = ucc -> tasksProgressService.broadcast(taskProgressMap.get(ucc.getTaskId()));

				Function<FileDto, UploadChunkContainer> packInContainer = fileDto -> new UploadChunkContainer(taskId, fileDto);
				Function<UploadChunkContainer, Stream<UploadChunkContainer>> parseDirectories = new DirectoryProcessor(filesIOService, statistics, performanceDataService);
				Function<UploadChunkContainer, UploadChunkContainer> generateFilehash = new FileHasher(instance, statistics, performanceDataService);
				Predicate<UploadChunkContainer> removeFilesWithSize0 = ucc -> ucc.getFileDto().getSize() > 0;
				Function<UploadChunkContainer, UploadChunkContainer> storeFile = new FileStorer(fileServices, account, markFileProcessed, broadcastTaskProgress);
				Function<UploadChunkContainer, Stream<UploadChunkContainer>> splitFileIntoChunks = new FileSplitter(account.getAttachmentSizeMB(), 2, statistics, performanceDataService);
				Function<UploadChunkContainer, UploadChunkContainer> generateChunkHash = new ChunkHasher(instance, statistics, performanceDataService);
				Function<UploadChunkContainer, UploadChunkContainer> chunkEncoder = new ChunkEncoder(cryptoService, account.getCryptoKey(), statistics, performanceDataService);
				Function<UploadChunkContainer, UploadChunkContainer> saveOnIMAPServer = new ChunkSaver(createConnectionPool(account), cryptoService, statistics, performanceDataService);
				Function<UploadChunkContainer, UploadChunkContainer> storeFileChunk = new FileChunkStorer(fileServices);

				selectedFiles.stream().parallel()
					.map(packInContainer)
					.flatMap(parseDirectories)
					.map(generateFilehash)
					.filter(removeFilesWithSize0)
					.map(storeFile)
					.filter(filterEmptyUcc)
					.flatMap(splitFileIntoChunks)
					.filter(filterEmptyUcc)
					.map(generateChunkHash)
					.map(chunkEncoder)
					.filter(filterEmptyUcc)
					.map(saveOnIMAPServer)
					.filter(filterEmptyUcc)
					.map(storeFileChunk)
					.peek(updateProgress)
					.peek(broadcastTaskProgress)
					.forEach(System.out::println);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		});
		taskMap.put(taskId, task);
		return true;
	}

	private GenericObjectPool<Store> createConnectionPool(Account account) {
		String key = account.getImapServerAddress();
		if (accountPoolMap.containsKey(key)) {
			return accountPoolMap.get(key);
		} else {
			IMAPConnectionFactory connectionFactory = new IMAPConnectionFactory(account.getLogin(), account.getPassword(), key);
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(account.getMaxConcurrentConnections());
			GenericObjectPool<Store> pool = new GenericObjectPool<>(connectionFactory, config);
			accountPoolMap.put(key, pool);
			return pool;
		}
	}
}
