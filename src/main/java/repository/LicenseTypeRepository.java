package repository;

import entity.LicenseTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseTypeRepository extends JpaRepository<LicenseTypeEntity, UUID> {
    Optional<LicenseTypeEntity> findByName(String name);
}