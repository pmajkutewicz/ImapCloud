package pl.pamsoft.imapcloud.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;
import pl.pamsoft.imapcloud.dto.EmailProviderInfoList;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;

import java.io.IOException;
import java.util.List;

public class AccountRestClient extends AbstractRestClient {

	private static final Logger LOG = LoggerFactory.getLogger(AccountRestClient.class);
	private static final String LIST_EMAIL_PROVIDERS = "/accounts/emailProviders";
	private static final String CREATE_ACCOUNT = "/accounts";
	private final String endpoint;

	public AccountRestClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public EmailProviderInfoList getAvailableEmailAccounts() throws IOException {
		try {
			HttpResponse<EmailProviderInfoList> response = Unirest.get(endpoint + LIST_EMAIL_PROVIDERS)
				.asObject(EmailProviderInfoList.class);
			throwExceptionIfNotValidResponse(response);
			return response.getBody();
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}

	public void createAccount(EmailProviderInfo selectedEmailProvider, String username, String password) throws IOException {
		try {
			HttpResponse<JsonNode> httpResponse = Unirest.post(endpoint + CREATE_ACCOUNT)
				.body(new CreateAccountRequest(username, password, selectedEmailProvider))
				.asJson();
			LOG.debug(httpResponse.toString());
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}

	public List<AccountDto> listAccounts() throws IOException {
		try {
			HttpResponse<ListAccountResponse> httpResponse = Unirest.get(endpoint + CREATE_ACCOUNT).asObject(ListAccountResponse.class);
			return httpResponse.getBody().getAccountDtos();
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}
}
