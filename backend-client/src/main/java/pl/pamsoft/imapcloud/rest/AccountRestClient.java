package pl.pamsoft.imapcloud.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.responses.EmailProviderInfoResponse;
import pl.pamsoft.imapcloud.responses.ListAccountResponse;

import java.io.IOException;
import java.util.List;

public class AccountRestClient extends AbstractRestClient {

	private static final Logger LOG = LoggerFactory.getLogger(AccountRestClient.class);
	private static final String LIST_EMAIL_PROVIDERS = "/accounts/emailProviders";
	private static final String CREATE_ACCOUNT = "/accounts";

	public AccountRestClient(String endpoint, String username, String pass) {
		super(endpoint, username, pass);
	}

	public EmailProviderInfoResponse getAvailableEmailAccounts() throws IOException {
		try {
			HttpResponse<EmailProviderInfoResponse> response = Unirest.get(endpoint + LIST_EMAIL_PROVIDERS).basicAuth(bAuthUsername, bAuthPassword)
				.asObject(EmailProviderInfoResponse.class);
			throwExceptionIfNotValidResponse(response);
			return response.getBody();
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}

	public void createAccount(EmailProviderInfo selectedEmailProvider, String username, String password) throws IOException {
		try {
			HttpResponse<JsonNode> httpResponse = Unirest.post(endpoint + CREATE_ACCOUNT).basicAuth(bAuthUsername, bAuthPassword)
				.body(new CreateAccountRequest(username, password, selectedEmailProvider))
				.asJson();
			LOG.debug(httpResponse.toString());
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}

	public List<AccountDto> listAccounts() throws IOException {
		try {
			HttpResponse<ListAccountResponse> httpResponse = Unirest.get(endpoint + CREATE_ACCOUNT).basicAuth(bAuthUsername, bAuthPassword).asObject(ListAccountResponse.class);
			throwExceptionIfNotValidResponse(httpResponse);
			return httpResponse.getBody().getAccount();
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}
}
