package pl.pamsoft.imapcloud.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.progress.TaskProgressDto;
import pl.pamsoft.imapcloud.responses.AbstractResponse;
import pl.pamsoft.imapcloud.responses.TaskProgressResponse;
import pl.pamsoft.imapcloud.services.websocket.TasksProgressService;

import java.util.List;

@RestController
@RequestMapping("task")
public class TaskProgressRestController {

	@Autowired
	private TasksProgressService tasksProgressService;

	@ApiOperation("Tasks progress info")
	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> getAllTasks() {
		List<TaskProgressDto> allTasks = tasksProgressService.findAllTasks();
		return new ResponseEntity<>(new TaskProgressResponse(allTasks), HttpStatus.OK);
	}

}
