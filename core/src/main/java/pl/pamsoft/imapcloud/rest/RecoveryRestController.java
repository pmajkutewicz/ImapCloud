package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.requests.StartRecoveryRequest;
import pl.pamsoft.imapcloud.responses.AbstractResponse;
import pl.pamsoft.imapcloud.services.RecoveryService;

@RestController
@RequestMapping("recovery")
public class RecoveryRestController {

	@Autowired
	private RecoveryService recoveryService;

	@RequestMapping(value = "start", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> startUpload(@RequestBody StartRecoveryRequest startRecoveryRequest) {
		boolean taskAdded = recoveryService.recover(startRecoveryRequest.getSelectedAccount());

		if (taskAdded) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}