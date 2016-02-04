package pl.pamsoft.imapcloud.rest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	public ResponseEntity<?> listFilesInDirectory(@RequestParam(required = false, defaultValue = ".") String dir) {
		File selectedDir = new File(dir);
		if (selectedDir.isDirectory()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<FileDto> files = filesService.listFilesInDir(selectedDir);
		return new ResponseEntity<>(new ListFilesInDirResponse(files), HttpStatus.OK);
	}
}
