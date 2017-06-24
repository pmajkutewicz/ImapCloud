package pl.pamsoft.imapcloud.config;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.dto.AccountInfo;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class AccountsSettingsTest {

	private String basicExample = "mail_ru: {\n" +
		"  domain: mail.ru,\n" +
		"  host: imap.mail.ru,\n" +
		"  loginType: WITH_DOMAIN,\n" +
		"  maxConcurrentConnections: 8,\n" +
		"  accountSizeMB: 9999999,\n" +
		"  maxFileSizeMB: 25\n" +
		"  }";

	@Test
	public void shouldLoadAccountData() throws IOException {
		AccountsSettings accountsSettings = spy(AccountsSettings.class);
		when(accountsSettings.getData()).thenReturn(IOUtils.toInputStream(basicExample, "UTF-8"));

		List<AccountInfo> emailProviders = accountsSettings.createEmailProviders().getAccountProviders();

		assertEquals(emailProviders.size(), 1);
		assertEquals(emailProviders.get(0).getDomain(), "mail.ru");
	}
}
