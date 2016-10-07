package pl.pamsoft.imapcloud.utils;

import org.springframework.stereotype.Component;
import pl.pamsoft.imapcloud.dto.GitRepositoryState;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class GitStatsUtil {

	private GitRepositoryState gitRepositoryState;

	public GitRepositoryState getGitRepositoryState() throws IOException {
		if (gitRepositoryState == null) {
			try (
				InputStream is = getClass().getClassLoader().getResourceAsStream("git.properties");
				InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
			) {
				Properties properties = new Properties();
				properties.load(reader);
				gitRepositoryState = new GitRepositoryState(properties);
			}
		}
		return gitRepositoryState;
	}
}
