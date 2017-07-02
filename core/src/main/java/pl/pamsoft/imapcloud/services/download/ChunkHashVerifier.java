package pl.pamsoft.imapcloud.services.download;

import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.util.List;
import java.util.function.Function;

public class ChunkHashVerifier extends AbstractHashVerifier implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	public ChunkHashVerifier(List<String> invalidFileIds) {
		super(invalidFileIds);
	}

	@Override
	protected String getCurrentHash(DownloadChunkContainer dcc) {
		return dcc.getChunkHash();
	}

	@Override
	protected String getExpectedHash(DownloadChunkContainer dcc) {
		return dcc.getChunkToDownload().getChunkHash();
	}
}
