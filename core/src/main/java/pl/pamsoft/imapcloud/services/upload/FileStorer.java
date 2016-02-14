package pl.pamsoft.imapcloud.services.upload;

import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.UploadChunkContainer;

import java.util.function.Function;

public class FileStorer implements Function<UploadChunkContainer, UploadChunkContainer> {

	private FileServices fileServices;
	private Account account;

	public FileStorer(FileServices fileServices, Account account) {
		this.fileServices = fileServices;
		this.account = account;
	}

	@Override
	public UploadChunkContainer apply(UploadChunkContainer ucc) {
		File savedFile = fileServices.saveFile(ucc, account);
		return UploadChunkContainer.addIds(ucc, savedFile.getId(), savedFile.getFileUniqueId());
	}
}
