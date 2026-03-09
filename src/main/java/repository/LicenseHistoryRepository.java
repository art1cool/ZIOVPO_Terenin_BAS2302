package repository;

import entity.LicenseHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LicenseHistoryRepository extends JpaRepository<LicenseHistoryEntity, UUID> {
}