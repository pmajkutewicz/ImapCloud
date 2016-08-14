package pl.pamsoft.imapcloud.rest;

import io.swagger.annotations.ApiOperation;
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
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.requests.RecoverRequest;
import pl.pamsoft.imapcloud.requests.StartRecoveryRequest;
import pl.pamsoft.imapcloud.responses.AbstractResponse;
import pl.pamsoft.imapcloud.responses.RecoveryResultsResponse;
import pl.pamsoft.imapcloud.services.FileServices;
import pl.pamsoft.imapcloud.services.RecoveryService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("recovery")
public class RecoveryRestController {

	@Autowired
	private RecoveryService recoveryService;

	@Autowired
	private FileServices fileServices;

	@ApiOperation("Start account recovery")
	@RequestMapping(value = "start", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> start(@RequestBody StartRecoveryRequest startRecoveryRequest) {
		boolean taskAdded = recoveryService.recover(startRecoveryRequest.getSelectedAccount());

		if (taskAdded) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation("Fetch available recovery results")
	@ResponseBody
	@RequestMapping(value = "results", method = RequestMethod.GET)
	public ResponseEntity<? extends AbstractResponse> getResults() {
		Map<String, List<RecoveredFileDto>> results = recoveryService.getResults();

		// get ids of recovered files
		Set<String> recoveredFilesIds = results.values()
			.stream().flatMap(Collection::stream).map(RecoveredFileDto::getFileUniqueId).collect(toSet());

		// check if some files already exists in db
		Map<String, File> filesInDb = fileServices.findUploadedFiles()
			.stream()
			.filter(i -> recoveredFilesIds.contains(i.getFileUniqueId()))
			.collect(toMap(File::getFileUniqueId, Function.identity()));

		// update RecoveredFileDto with above data
		results.values().stream().flatMap(Collection::stream)
			.forEach(recoveredFileDto -> {
				if (filesInDb.containsKey(recoveredFileDto.getFileUniqueId())) {
					recoveredFileDto.setInDb(true);
					recoveredFileDto.setCompletedInDb(filesInDb.get(recoveredFileDto.getFileUniqueId()).isCompleted());
				}
			});
		return new ResponseEntity<>(new RecoveryResultsResponse(results), HttpStatus.OK);
	}

	@ApiOperation("Recover selected files")
	@ResponseBody
	@RequestMapping(value = "recover", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> recover(@RequestBody RecoverRequest recoverRequest) {
		boolean taskAdded = recoveryService.recoverFiles(recoverRequest.getTaskId(), recoverRequest.getUniqueFilesIds());

		if (taskAdded) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
