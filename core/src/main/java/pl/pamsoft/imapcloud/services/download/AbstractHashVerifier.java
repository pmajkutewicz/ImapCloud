package pl.pamsoft.imapcloud.services.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.services.DownloadChunkContainer;

import java.util.List;
import java.util.function.Function;

abstract class AbstractHashVerifier implements Function<DownloadChunkContainer, DownloadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractHashVerifier.class);
	private List<String> invalidFileIds;

	AbstractHashVerifier(List<String> invalidFileIds) {
		this.invalidFileIds = invalidFileIds;
	}

	@Override
	public DownloadChunkContainer apply(DownloadChunkContainer dcc) {
		boolean validHash = getExpectedHash(dcc).equals(getCurrentHash(dcc));
		if (validHash) {
			return dcc;
		} else {
			LOG.debug("Invalid hash for {}", dcc.getChunkToDownload().getOwnerFile().getName());
			String fileUniqueId = dcc.getChunkToDownload().getOwnerFile().getFileUniqueId();
			invalidFileIds.add(fileUniqueId);
			return DownloadChunkContainer.EMPTY;
		}
	}

	protected abstract String getCurrentHash(DownloadChunkContainer dcc);

	protected abstract String getExpectedHash(DownloadChunkContainer dcc);
}
