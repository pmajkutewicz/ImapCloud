package pl.pamsoft.imapcloud.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepositoryCustom {

	@Override
	public Long getUsedSpace(String accountId) {
		//FIXME
		return 0L;
	}

}
