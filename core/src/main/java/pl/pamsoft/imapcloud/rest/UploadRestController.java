package pl.pamsoft.imapcloud.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.requests.StartUploadRequest;
import pl.pamsoft.imapcloud.responses.AbstractResponse;

@RestController
@RequestMapping("uploads")
public class UploadRestController {

	@RequestMapping(value = "start", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> startUpload(@RequestBody StartUploadRequest startUploadRequest) {

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
