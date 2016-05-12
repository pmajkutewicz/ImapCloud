package pl.pamsoft.imapcloud.services.download;

import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FileHashVerifier extends AbstractHashVerifier implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	public FileHashVerifier(ConcurrentHashMap<String, String> invalidFileIds) {
		super(invalidFileIds);
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		if (dcc.getChunkToDownload().isLastChunk()) {
			return super.apply(dcc);
		}
		return dcc;
	}

	@Override
	String getCurrentHash(DownloadChunkContainer dcc) {
		return dcc.getFileHash();
	}

	@Override
	String getExpectedHash(DownloadChunkContainer dcc) {
		return dcc.getChunkToDownload().getOwnerFile().getFileHash();
	}
}
