package pl.pamsoft.imapcloud.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {

	Account getById(Long id);
}
