package pl.pamsoft.imapcloud.config;

import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;
import pl.pamsoft.imapcloud.api.accounts.AccountService;
import pl.pamsoft.imapcloud.dto.AccountInfo;
import pl.pamsoft.imapcloud.dto.AccountProviderInfoList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AccountsSettings {

	private static final Logger LOG = LoggerFactory.getLogger(AccountsSettings.class);

	@Bean
	@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
	@SuppressWarnings("unchecked")
	public AccountProviderInfoList createAccountProviders(@Autowired List<AccountService> services) {
		Map<String, AccountService> serviceMap =
			services.stream()
				.collect(Collectors.toMap(AccountService::getType, Function.identity()));
		List<AccountInfo> result = new ArrayList<>();
		Yaml yaml = new Yaml();
		try {
			Map<String, Map<String, Object>> load = yaml.load(getData());
			for (Map.Entry<String, Map<String, Object>> mapEntry : load.entrySet()) {
				Map<String, Object> value = mapEntry.getValue();
				AccountInfo accountProvider = createAccountProvider(value);
				if (serviceMap.containsKey(accountProvider.getType())) {
					Collection<String> missingProperties = allRequiredPropertiesPresent(accountProvider.getAdditionalProperties(), serviceMap.get(accountProvider.getType()));
					if (missingProperties.isEmpty()) {
						LOG.info("{} ({}) accounts will be managed by {}, all required properties are provided",
							accountProvider.getHost(), accountProvider.getType(), serviceMap.get(accountProvider.getType()).getClass().getName());
						result.add(accountProvider);
					} else {
						LOG.warn("{} ({}) accounts won't be managed by {}, properties missing: {}",
							accountProvider.getHost(), accountProvider.getType(), serviceMap.get(accountProvider.getType()).getClass().getName(), StringUtils.join(missingProperties, ","));
					}
				} else {
					LOG.warn("Manager for {} is missing. {} won't be supported.", accountProvider.getType(), accountProvider.getHost());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new AccountProviderInfoList(Collections.unmodifiableList(result));
	}

	private Collection<String> allRequiredPropertiesPresent(Map<String, String> additionalProperties, AccountService accountService) {
		return accountService.getRequiredPropertiesNames().stream().filter(key -> !additionalProperties.containsKey(key)).collect(Collectors.toSet());
	}

	@VisibleForTesting
	protected InputStream getData() throws IOException {
		return new ClassPathResource("accounts.yml").getInputStream();
	}

	private AccountInfo createAccountProvider(Map<String, Object> values) {
		String type = (String) values.get("type");
		String host = (String) values.get("host");
		Integer sizeMB = (Integer) values.get("accountSizeMB");
		Integer attachmentSizeMB = (Integer) values.get("maxFileSizeMB");
		Integer maxConcurrentConnections = (Integer) values.get("maxConcurrentConnections");

		Map<String, String> props = values.entrySet().stream().filter(i -> !AccountInfo.getStandardFields().contains(i.getKey()))
			.collect(Collectors.toMap(Map.Entry::getKey, i -> String.valueOf(i.getValue())));

		return new AccountInfo(type, host, maxConcurrentConnections, sizeMB, attachmentSizeMB, props);
	}

}
