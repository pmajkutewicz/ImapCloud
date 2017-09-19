package pl.pamsoft.imapcloud.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.TaskProgress;

@Repository
public interface TaskProgressRepository extends JpaRepository<TaskProgress, Long>, TaskProgressRepositoryCustom {

	TaskProgress getById(String id);
}
