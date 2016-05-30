package pl.pamsoft.imapcloud.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class GitStatsUtil {

	private GitRepositoryState gitRepositoryState;

	public GitRepositoryState getGitRepositoryState() throws IOException {
		if (gitRepositoryState == null) {
			Properties properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));

			gitRepositoryState = new GitRepositoryState(properties);
		}
		return gitRepositoryState;
	}
}
