package pl.pamsoft.imapcloud.restic.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.entity.ResticMapping;
import pl.pamsoft.imapcloud.restic.ResticUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static pl.pamsoft.imapcloud.restic.ResticType.CONFIG;

@Controller
@RequestMapping("/restic")
public class ResticConfigController extends AbstractResticController {

	private static final Logger LOG = LoggerFactory.getLogger(ResticConfigController.class);
	private static final String CONFIG_NAME = "config";

	@RequestMapping(value = "{path}", method = RequestMethod.POST)
	public ResponseEntity<String> createRepo(@PathVariable String path, @RequestParam(required = false) Boolean create, HttpServletRequest request) {
		LOG.debug("Create config for {}", path);
		HttpStatus httpStatus = null != create && create && accountServices.getByResticName(path).isPresent() ? OK : I_AM_A_TEAPOT;
		return new ResponseEntity<>(ResticUtils.getHeaders(request), httpStatus);
	}

	@RequestMapping(value = "{path}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteRepo(@PathVariable String path, HttpServletRequest request) throws IOException {
		LOG.debug("Delete config for {}", path);
		delete(path, CONFIG, CONFIG_NAME);
		return new ResponseEntity<>(ResticUtils.getHeaders(request), OK);
	}

	@RequestMapping(value = "{path}/config", method = RequestMethod.HEAD)
	public ResponseEntity<String> checkIfConfigExists(@PathVariable String path, HttpServletRequest request) {
		LOG.debug("Checking if config for {} exist", path);
		Optional<AccountDto> account = accountServices.getByResticName(path);
		// CHECK IF CONFIG bloob exist in repo
		ResticMapping config = resticService.getByTypeAndResticId(CONFIG, "config", account.get().getId());
		return new ResponseEntity<>("dummy", ResticUtils.getHeaders(request), null == config ? NOT_FOUND : OK);
	}

	@RequestMapping(value = "{path}/config", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getConfig(@PathVariable String path, HttpServletRequest request) throws IOException {
		LOG.debug("Fetching config for {}", path);
		return new ResponseEntity<>(download(path, CONFIG, CONFIG_NAME), ResticUtils.getHeaders(request), OK);
	}

	@RequestMapping(value = "{path}/config", method = RequestMethod.POST)
	public ResponseEntity<Void> createConfig(@PathVariable String path, HttpServletRequest request) throws IOException {
		upload(path, CONFIG, CONFIG_NAME, request.getInputStream());
		return new ResponseEntity<>(ResticUtils.getHeaders(request), OK);
	}

}
