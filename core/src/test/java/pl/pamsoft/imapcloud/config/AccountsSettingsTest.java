package pl.pamsoft.imapcloud.config;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.dto.AccountInfo;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class AccountsSettingsTest {

	private String basicImapExample = "mail_ru: {\n" +
		"  type: imap,\n" +
		"  host: imap.mail.ru,\n" +
		"  loginType: WITH_DOMAIN,\n" +
		"  maxConcurrentConnections: 8,\n" +
		"  accountSizeMB: 9999999,\n" +
		"  maxFileSizeMB: 25\n" +
		"  }";

	private String additionalPropertiesExample = "mail_ru: {\n" +
		"  type: imap,\n" +
		"  host: imap.mail.ru,\n" +
		"  test: test,\n" +
		"  maxConcurrentConnections: 8,\n" +
		"  accountSizeMB: 9999999,\n" +
		"  maxFileSizeMB: 25\n" +
		"  }";

	@Test
	void shouldntLoadAccountWhenServiceIsNotFounded() throws IOException {
		AccountsSettings accountsSettings = spy(AccountsSettings.class);
		when(accountsSettings.getData()).thenReturn(IOUtils.toInputStream(basicImapExample, "UTF-8"));

		List<AccountInfo> emailProviders = accountsSettings.createAccountProviders(ImmutableList.of(create("invalid"))).getAccountProviders();

		assertEquals(0, emailProviders.size());
	}

	@Test
	void shouldLoadAccountData() throws IOException {
		AccountsSettings accountsSettings = spy(AccountsSettings.class);
		when(accountsSettings.getData()).thenReturn(IOUtils.toInputStream(basicImapExample, "UTF-8"));

		List<AccountInfo> emailProviders = accountsSettings.createAccountProviders(ImmutableList.of(create("imap"))).getAccountProviders();

		assertEquals(1, emailProviders.size());
		assertEquals(25, emailProviders.get(0).getMaxFileSizeMB().intValue());
		assertEquals(9999999, emailProviders.get(0).getAccountSizeMB().intValue());
		assertEquals(8, emailProviders.get(0).getMaxConcurrentConnections().intValue());
		assertEquals("imap.mail.ru", emailProviders.get(0).getHost());
	}

	@Test
	void shouldLoadAdditionalPropertiesInAccountData() throws IOException {
		AccountsSettings accountsSettings = spy(AccountsSettings.class);
		when(accountsSettings.getData()).thenReturn(IOUtils.toInputStream(additionalPropertiesExample, "UTF-8"));

		List<AccountInfo> emailProviders = accountsSettings.createAccountProviders(ImmutableList.of(create("imap"))).getAccountProviders();

		assertEquals(1, emailProviders.size());
		assertEquals("test", emailProviders.get(0).getProperty("test"));
	}

	AccountService create(String type) {
		AccountService mock = Mockito.mock(AccountService.class);
		Mockito.when(mock.getType()).thenReturn(type);
		return mock;
	}
}
