package pl.pamsoft.imapcloud.rest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.responses.AbstractResponse;
import pl.pamsoft.imapcloud.responses.ErrorResponse;
import pl.pamsoft.imapcloud.responses.GetHomeDirResponse;
import pl.pamsoft.imapcloud.responses.ListFilesInDirResponse;
import pl.pamsoft.imapcloud.services.FilesService;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("files")
public class FilesRestController {

	@Autowired
	private FilesService filesService;

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends AbstractResponse> listFilesInDirectory(@RequestParam(name = "dir") String dir) {
		File selectedDir = new File(dir);
		if (!selectedDir.isDirectory()) {
			return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), dir + " is not valid directory"), HttpStatus.BAD_REQUEST);
		}

		List<FileDto> files = filesService.listFilesInDir(selectedDir);
		return new ResponseEntity<>(new ListFilesInDirResponse(files), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "homeDir", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getHomeDir() {
		String homeDir = System.getProperty("user.home");
		return new ResponseEntity<>(new GetHomeDirResponse(homeDir), HttpStatus.OK);
	}
}
