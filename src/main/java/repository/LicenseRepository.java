package repository;

import entity.LicenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseEntity, UUID> {
    Optional<LicenseEntity> findByCode(String code);
}