package pl.pamsoft.imapcloud.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepositoryCustom {

	Long getUsedSpace(String accountId);

}
