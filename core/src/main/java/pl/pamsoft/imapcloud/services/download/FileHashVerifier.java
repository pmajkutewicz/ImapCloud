package pl.pamsoft.imapcloud.services.download;

import pl.pamsoft.imapcloud.services.containers.DownloadChunkContainer;

import java.util.List;
import java.util.function.Function;

public class FileHashVerifier extends AbstractHashVerifier implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	public FileHashVerifier(List<String> invalidFileIds) {
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
	protected String getCurrentHash(DownloadChunkContainer dcc) {
		return dcc.getFileHash();
	}

	@Override
	protected String getExpectedHash(DownloadChunkContainer dcc) {
		return dcc.getChunkToDownload().getOwnerFile().getFileHash();
	}
}
