package pl.pamsoft.imapcloud.services.download;

import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ChunkHashVerifier extends AbstractHashVerifier implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	public ChunkHashVerifier(ConcurrentHashMap<String, String> invalidFileIds) {
		super(invalidFileIds);
	}

	@Override
	String getCurrentHash(DownloadChunkContainer dcc) {
		return dcc.getChunkHash();
	}

	@Override
	String getExpectedHash(DownloadChunkContainer dcc) {
		return dcc.getChunkToDownload().getChunkHash();
	}
}
