package pl.pamsoft.imapcloud.restic;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.restic.controllers.ResticGeneralController;
import pl.pamsoft.imapcloud.services.AccountServices;

import java.util.Optional;

@Aspect

@Component
public class AccountAspect {

	private static final Logger LOG = LoggerFactory.getLogger(ResticGeneralController.class);

	private AccountServices accountServices;

	@Before(value = "execution(* pl.pamsoft.imapcloud.restic.controllers.*.*(..)) && args(path, ..) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", argNames = "joinPoint,path")
	public void before(JoinPoint joinPoint, String path) {
		Optional<AccountDto> account = accountServices.getByResticName(path);
		if (!account.isPresent()) {
			LOG.debug("404: {}", joinPoint.getSignature().toShortString());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
		}
	}

	@Autowired
	public void setAccountServices(AccountServices accountServices) {
		this.accountServices = accountServices;
	}
}
