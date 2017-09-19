package pl.pamsoft.imapcloud.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.EntryProgress;

@Repository
public interface FileProgressRepository extends JpaRepository<EntryProgress, Long> {

}
