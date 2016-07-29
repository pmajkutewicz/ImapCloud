package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.RecoveredFileDto;
import pl.pamsoft.imapcloud.requests.RecoverRequest;
import pl.pamsoft.imapcloud.requests.StartRecoveryRequest;
import pl.pamsoft.imapcloud.responses.AbstractResponse;
import pl.pamsoft.imapcloud.responses.RecoveryResultsResponse;
import pl.pamsoft.imapcloud.services.RecoveryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("recovery")
public class RecoveryRestController {

	@Autowired
	private RecoveryService recoveryService;

	@RequestMapping(value = "start", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> start(@RequestBody StartRecoveryRequest startRecoveryRequest) {
		boolean taskAdded = recoveryService.recover(startRecoveryRequest.getSelectedAccount());

		if (taskAdded) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@RequestMapping(value = "results", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> getResults() {
		Map<String, List<RecoveredFileDto>> results = recoveryService.getResults();
		return new ResponseEntity<>(new RecoveryResultsResponse(results), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "recover", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> recover(@RequestBody RecoverRequest recoverRequest) {
		boolean taskAdded = recoveryService.recoverFiles(recoverRequest.getUniqueFilesIds());

		if (taskAdded) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
