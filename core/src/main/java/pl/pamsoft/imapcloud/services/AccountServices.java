package pl.pamsoft.imapcloud.services;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountServices {

	private static final Logger LOG = LoggerFactory.getLogger(AccountServices.class);
	private AccountRepository accountRepository;
	private CryptoService cryptoService;

	private Function<? super Account, AccountDto> toAccount = a -> {
		long usedSpace = accountRepository.getUsedSpace(a.getLogin());
		return new AccountDto(a.getId(), String.format("%s@%s", a.getLogin(), a.getHost()), a.getCryptoKey(), usedSpace);
	};

	public void addAccount(CreateAccountRequest request) {
		Account account = new Account();

		account.setLogin(request.getUsername());
		account.setPassword(request.getPassword());
		account.setType(request.getSelectedAccountProvider().getType());
		account.setHost(request.getSelectedAccountProvider().getHost());
		account.setAccountSizeMB(request.getSelectedAccountProvider().getAccountSizeMB());
		account.setAttachmentSizeMB(request.getSelectedAccountProvider().getMaxFileSizeMB());
		account.setMaxConcurrentConnections(request.getSelectedAccountProvider().getMaxConcurrentConnections());
		account.setCryptoKey(getCryptoKey(request));
		account.setAdditionalProperties(request.getSelectedAccountProvider().getAdditionalProperties());

		accountRepository.save(account);

	}

	@VisibleForTesting
	protected String getCryptoKey(CreateAccountRequest request) {
		try {
			if (Strings.isNullOrEmpty(request.getCryptoKey())) {
				return ByteUtils.toHexString(cryptoService.generateKey());
			} else {
				try {
					return ByteUtils.toHexString(cryptoService.calcSha256(request.getCryptoKey()));
				} catch (UnsupportedEncodingException e) {
					LOG.error("Can't hash passphrase");
					return ByteUtils.toHexString(cryptoService.generateKey());
				}
			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			LOG.error("Can't create encryption key.", e);
		}
		return ByteUtils.toHexString(cryptoService.generateWeakKey());
	}

	public List<AccountDto> listAccounts() {
		Collection<Account> all = accountRepository.findAll();
		return all.stream()
			.map(toAccount)
			.collect(Collectors.toList());
	}

	@Autowired
	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	public void setCryptoService(CryptoService cryptoService) {
		this.cryptoService = cryptoService;
	}
}
