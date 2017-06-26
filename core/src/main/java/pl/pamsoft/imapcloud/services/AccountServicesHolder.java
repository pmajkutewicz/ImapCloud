package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.api.accounts.AccountService;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountServicesHolder {

	@Autowired
	private Collection<? extends AccountService> accountServices;

	private Map<String, AccountService> accountServiceMap;

	@PostConstruct
	public void init() {
		accountServiceMap = accountServices.stream().collect(Collectors.toMap(AccountService::getType, Function.identity()));
	}

	public AccountService getAccountService(String type) {
		return accountServiceMap.get(type);
	}
}
