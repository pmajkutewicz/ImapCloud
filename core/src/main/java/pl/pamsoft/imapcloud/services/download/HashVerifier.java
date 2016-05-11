package pl.pamsoft.imapcloud.services.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class HashVerifier implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(HashVerifier.class);
	private ConcurrentHashMap<String, String> invalidFileIds;

	public HashVerifier(ConcurrentHashMap<String, String> invalidFileIds) {
		this.invalidFileIds = invalidFileIds;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		boolean validHash = dcc.getChunkToDownload().getChunkHash().equals(dcc.getChunkHash());
		if (validHash) {
			return dcc;
		} else {
			LOG.debug("Invalid hash for {}", dcc.getChunkToDownload().getOwnerFile().getName());
			String fileUniqueId = dcc.getChunkToDownload().getOwnerFile().getFileUniqueId();
			invalidFileIds.put(fileUniqueId, fileUniqueId);
			return DownloadChunkContainer.EMPTY;
		}
	}
}
