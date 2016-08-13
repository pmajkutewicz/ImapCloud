package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.utils.GitRepositoryState;
import pl.pamsoft.imapcloud.utils.GitStatsUtil;

import java.io.IOException;

@RestController
@RequestMapping("/git")
public class GitStatsController {

	@Autowired
	private GitStatsUtil gitStatsUtil;

	@RequestMapping("/status")
	public ResponseEntity<GitRepositoryState> checkGitRevision() throws IOException {
		return new ResponseEntity<GitRepositoryState>(gitStatsUtil.getGitRepositoryState(), HttpStatus.OK);

}
}
