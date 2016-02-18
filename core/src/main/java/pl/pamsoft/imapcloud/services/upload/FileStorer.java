package pl.pamsoft.imapcloud.services.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.nio.file.FileAlreadyExistsException;
import java.util.function.Function;

public class FileStorer implements Function<UploadChunkContainer, UploadChunkContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(FileStorer.class);

	private FileServices fileServices;
	private Account account;

	public FileStorer(FileServices fileServices, Account account) {
		this.fileServices = fileServices;
		this.account = account;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer ucc) {
		try {
			File savedFile = fileServices.saveFile(ucc, account);
			return UploadChunkContainer.addIds(ucc, savedFile.getId(), savedFile.getFileUniqueId());
		} catch (FileAlreadyExistsException e) {
			LOG.warn("{} removed from queue.", ucc.getFileDto().getAbsolutePath());
			return UploadChunkContainer.EMPTY;
		}
	}
}
