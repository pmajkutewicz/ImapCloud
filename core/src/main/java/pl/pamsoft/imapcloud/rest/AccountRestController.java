package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.EmailProviderInfoList;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;
import pl.pamsoft.imapcloud.services.AccountServices;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountRestController {

	@Autowired
	private EmailProviderInfoList supportedEmailProviders;

	@Autowired
	private AccountServices accountServices;

	@RequestMapping(value = "emailProviders", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public EmailProviderInfoList listSupportedEmailProviders() {
		return supportedEmailProviders;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
		accountServices.addAccount(createAccountRequest);
	}

	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ListAccountResponse listAccounts() {
		List<AccountDto> accountDtos = accountServices.listAccounts();
		return new ListAccountResponse(accountDtos);
	}
}
