package pl.pamsoft.imapcloud.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.AccountProviderInfoList;
import pl.pamsoft.imapcloud.requests.AccountCapacityTestRequest;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.AccountProviderInfoResponse;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;
import pl.pamsoft.imapcloud.services.AccountServices;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountRestController {

	@Autowired
	private AccountProviderInfoList supportedAccountProviders;

	@Autowired
	private AccountServices accountServices;

	@ApiOperation("List supported account providers")
	@RequestMapping(value = "accountProviders", method = RequestMethod.GET)
	public AccountProviderInfoResponse listSupportedAccountProviders() {
		return new AccountProviderInfoResponse(supportedAccountProviders.getAccountProviders());
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

	@ApiOperation("Returns list of accounts")
	@RequestMapping(value = "testCapacity", method = RequestMethod.POST)
	public void testAccountCapacity(@RequestBody AccountCapacityTestRequest accountCapacityTestRequest) {
		accountServices.testAccountCapacity(accountCapacityTestRequest);
	}
}
