package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;

import java.util.List;

@Service
public class UploadService {

	@Autowired
	private AccountRepository accountRepository;

	public void upload(AccountDto selectedAccount, List<FileDto> selectedFiles) {
		Account account = accountRepository.getById(selectedAccount.getId());

		selectedFiles.stream()
			.flatMap(new FileSplitter(account.getAttachmentSizeMB()))
			.forEach(a -> System.out.println(a.capacity()));

		System.out.println(account);
//		new FileChunkIterator(selectedAccount.)
//		selectedFiles.stream().
	}

}
