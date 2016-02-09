package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.LoginType;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountServices {

	@Autowired
	private AccountRepository accountRepository;

	private Function<? super Account, AccountDto> toAccount = a -> new AccountDto(a.getId(), a.getEmail(), 0); //TODO: update used space

	public void addAccount(CreateAccountRequest request) {
		Account account = new Account();
		String email = request.getUsername() + '@' + request.getSelectedEmailProvider().getDomain();
		account.setEmail(request.getUsername());
		if (LoginType.USERNAME_ONLY == request.getSelectedEmailProvider().getLoginType()) {
			account.setLogin(request.getUsername());
		} else {
			account.setLogin(email);
		}
		account.setPassword(request.getPassword());
		account.setImapServerAddress(request.getSelectedEmailProvider().getImapHost());
		account.setSizeMB(request.getSelectedEmailProvider().getSizeMB());
		account.setAttachmentSizeMB(request.getSelectedEmailProvider().getAttachmentSizeMB());

		accountRepository.save(account);
	}

	public List<AccountDto> listAccounts() {
		Collection<Account> all = accountRepository.findAll();
		return all.stream().map(toAccount).collect(Collectors.toList());
	}
}
