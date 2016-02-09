package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.requests.StartUploadRequest;
import pl.pamsoft.imapcloud.responses.AbstractResponse;
import pl.pamsoft.imapcloud.services.UploadService;

@RestController
@RequestMapping("uploads")
public class UploadRestController {

	@Autowired
	private UploadService uploadService;

	@RequestMapping(value = "start", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> startUpload(@RequestBody StartUploadRequest startUploadRequest) {
		uploadService.upload(startUploadRequest.getSelectedAccount(), startUploadRequest.getSelectedFiles());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
