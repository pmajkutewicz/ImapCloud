package pl.pamsoft.imapcloud.services.verify;

import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.services.containers.VerifyChunkContainer;

import java.util.function.Function;

public class UpdateVerifyInfoInDb implements Function<VerifyChunkContainer, VerifyChunkContainer> {

	private FileChunkRepository fileChunkRepository;

	public UpdateVerifyInfoInDb(FileChunkRepository fileChunkRepository) {
		this.fileChunkRepository = fileChunkRepository;
	}

	@Override
	public VerifyChunkContainer apply(VerifyChunkContainer vcc) {
		fileChunkRepository.markChunkVerified(vcc.getDbChunkId(), vcc.getChunkExist());
		return VerifyChunkContainer.markAsUpdatedInDb(vcc);
	}
}
