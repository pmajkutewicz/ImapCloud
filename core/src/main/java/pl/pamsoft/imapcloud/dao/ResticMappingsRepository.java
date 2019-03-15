package pl.pamsoft.imapcloud.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pamsoft.imapcloud.entity.ResticMapping;
import pl.pamsoft.imapcloud.restic.ResticType;

import java.util.Collection;

@Repository
public interface ResticMappingsRepository extends JpaRepository<ResticMapping, Long> {

	ResticMapping getByTypeAndResticId(ResticType type, String restticId);

	ResticMapping getByTypeAndResticIdAndOwnerAccountId(ResticType type, String resticId, long ownerAccountId);

	Collection<ResticMapping> findByTypeAndOwnerAccountId(ResticType type, long ownerAccountId);

	void deleteByTypeAndResticIdAndOwnerAccountId(ResticType type, String resticId, long ownerAccountId);
}
