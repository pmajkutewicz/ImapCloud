package pl.pamsoft.imapcloud.api.containers;

import pl.pamsoft.imapcloud.api.accounts.Account;

public interface RecoveryChunkContainer {
	String getTaskId();

	Account getAccount();

}
