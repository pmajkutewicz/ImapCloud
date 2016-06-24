package pl.pamsoft.imapcloud.services;

import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dto.AccountDto;

@Service
public class RecoveryService extends AbstractBackgroundService {

	public boolean recover(AccountDto selectedAccount) {
		return true;
	}

	@Override
	int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	String getNameFormat() {
		return "RecoveryTask-%d";
	}
}
