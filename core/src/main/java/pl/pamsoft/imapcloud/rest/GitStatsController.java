package pl.pamsoft.imapcloud.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.GitRepositoryState;
import pl.pamsoft.imapcloud.utils.GitStatsUtil;

import java.io.IOException;

@RestController
@RequestMapping("/git")
public class GitStatsController {

	@Autowired
	private GitStatsUtil gitStatsUtil;

	@ApiOperation("Returns build info")
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public ResponseEntity<GitRepositoryState> checkGitRevision() throws IOException {
		return new ResponseEntity<>(gitStatsUtil.getGitRepositoryState(), HttpStatus.OK);
	}
}
