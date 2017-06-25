package pl.pamsoft.imapcloud.config;

import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.dto.AccountProviderInfoList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class AccountsSettings {

	@Bean
	@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
	@SuppressWarnings("unchecked")
	public AccountProviderInfoList createAccountProviders() {
		List<AccountInfo> result = new ArrayList<>();
		Yaml yaml = new Yaml();
		try {
			Map<String, Map<String, Object>> load = (Map<String, Map<String, Object>>) yaml.load(getData());
			for (Map.Entry<String, Map<String, Object>> mapEntry : load.entrySet()) {
				Map<String, Object> value = mapEntry.getValue();
				result.add(createAccountProvider(value));
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

	private AccountInfo createAccountProvider(Map<String, Object> values) {
		String host = (String) values.get("host");
		Integer sizeMB = (Integer) values.get("accountSizeMB");
		Integer attachmentSizeMB = (Integer) values.get("maxFileSizeMB");
		Integer maxConcurrentConnections = (Integer) values.get("maxConcurrentConnections");

		Map<String, String> props = values.entrySet().stream().filter(i -> !AccountInfo.getStandardFields().contains(i.getKey()))
			.collect(Collectors.toMap(Map.Entry::getKey, i -> String.valueOf(i.getValue())));

		return new AccountInfo(host, maxConcurrentConnections, sizeMB, attachmentSizeMB, props);
	}

}
