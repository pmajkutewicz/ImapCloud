package pl.pamsoft.imapcloud.utils;

import org.springframework.stereotype.Component;

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
			Properties properties = new Properties();
			InputStream is = getClass().getClassLoader().getResourceAsStream("git.properties");
			properties.load(new InputStreamReader(is, StandardCharsets.UTF_8));
			gitRepositoryState = new GitRepositoryState(properties);
		}
		return gitRepositoryState;
	}
}
