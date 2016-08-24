package pl.pamsoft.imapcloud.monitoring;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import org.springframework.stereotype.Component;

import static pl.pamsoft.imapcloud.common.StatisticType.CHUNK_DECRYPTER;
import static pl.pamsoft.imapcloud.common.StatisticType.CHUNK_DOWNLOADER;
import static pl.pamsoft.imapcloud.common.StatisticType.CHUNK_ENCRYPTER;
import static pl.pamsoft.imapcloud.common.StatisticType.CHUNK_HASH;
import static pl.pamsoft.imapcloud.common.StatisticType.CHUNK_RECOVERY;
import static pl.pamsoft.imapcloud.common.StatisticType.CHUNK_SAVER;
import static pl.pamsoft.imapcloud.common.StatisticType.CHUNK_VERIFIER;
import static pl.pamsoft.imapcloud.common.StatisticType.DIRECTORY_PROCESSOR;
import static pl.pamsoft.imapcloud.common.StatisticType.DIRECTORY_SIZE_CALCULATOR;
import static pl.pamsoft.imapcloud.common.StatisticType.FILE_CHUNK_CREATOR;
import static pl.pamsoft.imapcloud.common.StatisticType.FILE_DELETER;
import static pl.pamsoft.imapcloud.common.StatisticType.FILE_HASH;
import static pl.pamsoft.imapcloud.common.StatisticType.FILE_SAVER;
import static pl.pamsoft.imapcloud.dto.monitoring.MonitorDescription.OTHER;
import static pl.pamsoft.imapcloud.dto.monitoring.MonitorDescription.desc;
import static pl.pamsoft.imapcloud.websocket.TaskType.DELETE;
import static pl.pamsoft.imapcloud.websocket.TaskType.DOWNLOAD;
import static pl.pamsoft.imapcloud.websocket.TaskType.RECOVERY;
import static pl.pamsoft.imapcloud.websocket.TaskType.UPLOAD;
import static pl.pamsoft.imapcloud.websocket.TaskType.VERIFY;

@Component
public class Keys {
	public static final MonKey EXECUTOR_ACTIVE = new MonKeyImp("pl.pamsoft.imapcloud.services.AbstractBackgroundService.active", OTHER, "thread");
	public static final MonKey EXECUTOR_QUEUE = new MonKeyImp("pl.pamsoft.imapcloud.services.AbstractBackgroundService.queue", OTHER, "task");
	public static final MonKey IMAP_THROUGHPUT = new MonKeyImp("pl.pamsoft.imapcloud.imap.ChunkSaver.throughput", desc(CHUNK_SAVER, UPLOAD), "bytes/s");

	private static final String MS = "ms.";
	public static final MonKey UL_DIRECTORY_SIZE_CALC = new MonKeyImp("pl.pamsoft.imapcloud.services.upload.DirectorySizeCalculator", desc(DIRECTORY_SIZE_CALCULATOR, UPLOAD), MS);
	public static final MonKey UL_DIRECTORY_PROCESSOR = new MonKeyImp("pl.pamsoft.imapcloud.services.upload.DirectoryProcessor", desc(DIRECTORY_PROCESSOR, UPLOAD), MS);
	public static final MonKey UL_FILE_HASHER = new MonKeyImp("pl.pamsoft.imapcloud.services.upload.UploadFileHasher", desc(FILE_HASH, UPLOAD), MS);
	public static final MonKey UL_FILE_CHUNK_CREATOR = new MonKeyImp("pl.pamsoft.imapcloud.services.upload.FileChunkIterator", desc(FILE_CHUNK_CREATOR, UPLOAD), MS);
	public static final MonKey UL_CHUNK_HASHER = new MonKeyImp("pl.pamsoft.imapcloud.services.upload.UploadChunkHasher", desc(CHUNK_HASH, UPLOAD), MS);
	public static final MonKey UL_CHUNK_ENCRYPTER = new MonKeyImp("pl.pamsoft.imapcloud.services.upload.ChunkEncrypter", desc(CHUNK_ENCRYPTER, UPLOAD), MS);
	public static final MonKey UL_CHUNK_SAVER = new MonKeyImp("pl.pamsoft.imapcloud.services.upload.ChunkSaver", desc(CHUNK_SAVER, UPLOAD), MS);
	public static final MonKey DL_CHUNK_LOADER = new MonKeyImp("pl.pamsoft.imapcloud.imap.ChunkLoader", desc(CHUNK_DOWNLOADER, DOWNLOAD), MS);
	public static final MonKey DL_CHUNK_DECRYPTER = new MonKeyImp("pl.pamsoft.imapcloud.services.download.ChunkDecrypter", desc(CHUNK_DECRYPTER, UPLOAD), MS);
	public static final MonKey DL_CHUNK_HASHER = new MonKeyImp("pl.pamsoft.imapcloud.services.download.DownloadChunkHasher", desc(CHUNK_HASH, DOWNLOAD), MS);
	public static final MonKey DL_CHINK_APPENDER = new MonKeyImp("pl.pamsoft.imapcloud.services.download.FileSaver", desc(FILE_SAVER, DOWNLOAD), MS);
	public static final MonKey DL_FILE_HASHER = new MonKeyImp("pl.pamsoft.imapcloud.services.download.DownloadFileHasher", desc(FILE_HASH, DOWNLOAD), MS);
	public static final MonKey VR_CHUNK_VERIFIER = new MonKeyImp("pl.pamsoft.imapcloud.ChunkVerifier", desc(CHUNK_VERIFIER, VERIFY), MS);
	public static final MonKey RE_CHUNK_RECOVERY = new MonKeyImp("pl.pamsoft.imapcloud.imap.ChunkRecovery", desc(CHUNK_RECOVERY, RECOVERY), MS);
	public static final MonKey DE_FILE_DELETER = new MonKeyImp("pl.pamsoft.imapcloud.imap.FileDeleter", desc(FILE_DELETER, DELETE), MS);
}
