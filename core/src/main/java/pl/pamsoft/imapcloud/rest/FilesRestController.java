package pl.pamsoft.imapcloud.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.responses.ListFilesInDirResponse;
import pl.pamsoft.imapcloud.services.FilesService;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("files")
public class FilesRestController {

	@Autowired
	private FilesService filesService;

	@ResponseBody
	public ResponseEntity<?> listFilesInDirectory(@RequestParam(required = false, defaultValue = ".") String dir) {
		File selectedDir = new File(dir);
		throwExceptionWhenNotDirectory(selectedDir);

		List<FileDto> files = filesService.listFilesInDir(selectedDir);
		return new ResponseEntity<>(new ListFilesInDirResponse(files), HttpStatus.OK);
	}

	private void throwExceptionWhenNotDirectory(File selectedDir) {
		if (selectedDir.isDirectory()) {
			new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
}
