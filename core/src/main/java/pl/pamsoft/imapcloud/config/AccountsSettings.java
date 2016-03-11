package pl.pamsoft.imapcloud.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;
import pl.pamsoft.imapcloud.dto.EmailProviderInfo;
import pl.pamsoft.imapcloud.dto.EmailProviderInfoList;
import pl.pamsoft.imapcloud.dto.LoginType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class AccountsSettings {

	@Bean
	@SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
	@SuppressWarnings("unchecked")
	public EmailProviderInfoList createEmailProviders() {
		List<EmailProviderInfo> result = new ArrayList<>();
		Yaml yaml = new Yaml();
		try {
			Map<String, Map<String, Object>> load = (Map<String, Map<String, Object>>) yaml.load(new ClassPathResource("accounts.yml").getInputStream());
			for (Map.Entry<String, Map<String, Object>> mapEntry : load.entrySet()) {
				Map<String, Object> value = mapEntry.getValue();
				result.add(createEmailProvider(value));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new EmailProviderInfoList(Collections.unmodifiableList(result));
	}

	private EmailProviderInfo createEmailProvider(Map<String, Object> values) {
		String domain = (String) values.get("domain");
		String imapHost = (String) values.get("imapHost");
		LoginType loginType = LoginType.valueOf((String) values.get("loginType"));
		Integer sizeMB = (Integer) values.get("sizeMB");
		Integer attachmentSizeMB = (Integer) values.get("attachmentSizeMB");
		Integer maxConcurrentConnections = (Integer) values.get("maxConcurrentConnections");
		return new EmailProviderInfo(domain, imapHost, loginType, maxConcurrentConnections, sizeMB, attachmentSizeMB);
	}

}
