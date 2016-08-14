package pl.pamsoft.imapcloud.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.EmailProviderInfoList;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.EmailProviderInfoResponse;
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

	@ApiOperation("List supported email providers")
	@RequestMapping(value = "emailProviders", method = RequestMethod.GET)
	public EmailProviderInfoResponse listSupportedEmailProviders() {
		return new EmailProviderInfoResponse(supportedEmailProviders.getEmailProviders());
	}

	@ApiOperation("Creates new account")
	@RequestMapping(method = RequestMethod.POST)
	public void createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
		accountServices.addAccount(createAccountRequest);
	}

	@ApiOperation("Returns list of accounts")
	@RequestMapping(method = RequestMethod.GET)
	public ListAccountResponse listAccounts() {
		List<AccountDto> accountDtos = accountServices.listAccounts();
		return new ListAccountResponse(accountDtos);
	}
}
