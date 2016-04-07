package pl.pamsoft.imapcloud.services;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.imap.ChunkVerifier;
import pl.pamsoft.imapcloud.mbeans.Statistics;
import pl.pamsoft.imapcloud.services.websocket.PerformanceDataService;

import javax.mail.Store;
import java.util.List;

@Service
public class VerificationService {

	@Autowired
	private ConnectionPoolService connectionPoolService;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private FileChunkRepository fileChunkRepository;

	@Autowired
	private Statistics statistics;

	@Autowired
	private PerformanceDataService performanceDataService;

	public void validate(List<FileChunk> fileChunks) {
		fileChunks.stream().parallel()
			.forEach(chunk -> {
				GenericObjectPool<Store> connectionPool = connectionPoolService.getOrCreatePoolForAccount(chunk.getOwnerFile().getOwnerAccount());
				ChunkVerifier chunkVerifier = new ChunkVerifier(connectionPool, cryptoService, statistics, performanceDataService);
				Boolean chunkExists = chunkVerifier.apply(chunk);
				fileChunkRepository.markChunkVerified(chunk.getId(), chunkExists);
				}
			);
	}
}
