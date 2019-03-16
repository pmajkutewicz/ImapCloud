package pl.pamsoft.imapcloud.restic.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.restic.ResticType;
import pl.pamsoft.imapcloud.restic.ResticTypeConverter;
import pl.pamsoft.imapcloud.restic.ResticUtils;
import pl.pamsoft.imapcloud.restic.dto.V1Result;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/restic")
public class ResticGeneralController extends AbstractResticController {

	private static final Logger LOG = LoggerFactory.getLogger(ResticGeneralController.class);

	@Autowired
	private ObjectMapper mapper;

	@InitBinder
	public void initBinder(final WebDataBinder webdataBinder) {
		webdataBinder.registerCustomEditor(ResticType.class, new ResticTypeConverter());
	}

	@RequestMapping(value = "{path}/{type}", method = GET)
	public ResponseEntity<String> get(@PathVariable String path, @PathVariable ResticType type, HttpServletRequest request) throws JsonProcessingException {
		LOG.info("Restic received {}: {}/{}", request.getMethod(), path, type);
		Optional<AccountDto> account = accountServices.getByResticName(path);
		Collection<? extends V1Result> result = resticService.findByTypeAndOwnerAccountId(type, ResticUtils.getAPIVersion(request), account.get().getId());
		return new ResponseEntity<>(mapper.writeValueAsString(result), ResticUtils.getHeaders(request), HttpStatus.OK);
	}

	@RequestMapping(value = "{path}/{type}/{name}", method = GET)
	public ResponseEntity<byte[]> get(@PathVariable String path, @PathVariable ResticType type, @PathVariable String name, HttpServletRequest request, @RequestHeader("Range") String range) throws IOException {
		LOG.info("Restic received {}: {}/{}/{} ({})", request.getMethod(), path, type, name, range);
		HttpRange httpRange = HttpRange.parseRanges(range).get(0);

		byte[] download = download(path, type, name);
		byte[] bytes = Arrays.copyOfRange(download, (int) httpRange.getRangeStart(download.length), (int) httpRange.getRangeEnd(download.length)+1);

		return new ResponseEntity<>(bytes, ResticUtils.getHeaders(request, true), download.length == bytes.length ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT);
	}

	@RequestMapping(value = "{path}/{type}/{name}", method = RequestMethod.POST)
	public ResponseEntity<String> post(@PathVariable String path, @PathVariable ResticType type, @PathVariable String name, HttpServletRequest request) throws IOException {
		LOG.info("Restic received {}: {}/{}/{}", request.getMethod(), path, type, name);
		upload(path, type, name, request.getInputStream());
		return new ResponseEntity<>(ResticUtils.getHeaders(request), HttpStatus.OK);
	}

	@RequestMapping(value = "{path}/{type}/{name}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable String path, @PathVariable ResticType type, @PathVariable String name, HttpServletRequest request) throws IOException {
		LOG.info("Restic received {}: {}/{}/{}", request.getMethod(), path, type, name);
		delete(path, type, name);
		return new ResponseEntity<>(ResticUtils.getHeaders(request), HttpStatus.OK);
	}
}
