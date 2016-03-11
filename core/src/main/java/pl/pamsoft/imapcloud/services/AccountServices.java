package pl.pamsoft.imapcloud.services;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.LoginType;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;

import java.nio.file.FileAlreadyExistsException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountServices {

	private static final Logger LOG = LoggerFactory.getLogger(AccountServices.class);

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CryptoService cryptoService;

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
		account.setMaxConcurrentConnections(request.getSelectedEmailProvider().getMaxConcurrentConnections());

		try {
			byte[] keyBytes = cryptoService.generateKey();
			account.setCryptoKey(ByteUtils.toHexString(keyBytes));
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			LOG.error("Can't create encryption key.", e);
		}
		try {
			accountRepository.save(account);
		} catch (FileAlreadyExistsException e) {
			LOG.warn("Account already exists.");
		}
	}

	public List<AccountDto> listAccounts() {
		Collection<Account> all = accountRepository.findAll();
		return all.stream().map(toAccount).collect(Collectors.toList());
	}
}
