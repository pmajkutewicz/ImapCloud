package pl.pamsoft.imapcloud.config;

import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.dto.AccountProviderInfoList;
import pl.pamsoft.imapcloud.dto.LoginType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class AccountsSettings {

	@Bean
	@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
	@SuppressWarnings("unchecked")
	public AccountProviderInfoList createEmailProviders() {
		List<AccountInfo> result = new ArrayList<>();
		Yaml yaml = new Yaml();
		try {
			Map<String, Map<String, Object>> load = (Map<String, Map<String, Object>>) yaml.load(getData());
			for (Map.Entry<String, Map<String, Object>> mapEntry : load.entrySet()) {
				Map<String, Object> value = mapEntry.getValue();
				result.add(createEmailProvider(value));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new AccountProviderInfoList(Collections.unmodifiableList(result));
	}

	@VisibleForTesting
	protected InputStream getData() throws IOException {
		return new ClassPathResource("accounts.yml").getInputStream();
	}

	private AccountInfo createEmailProvider(Map<String, Object> values) {
		String domain = (String) values.get("domain");
		String imapHost = (String) values.get("host");
		LoginType loginType = LoginType.valueOf((String) values.get("loginType"));
		Integer sizeMB = (Integer) values.get("accountSizeMB");
		Integer attachmentSizeMB = (Integer) values.get("maxFileSizeMB");
		Integer maxConcurrentConnections = (Integer) values.get("maxConcurrentConnections");
		return new AccountInfo(domain, imapHost, loginType, maxConcurrentConnections, sizeMB, attachmentSizeMB);
	}

}
