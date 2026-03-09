package repository;

import entity.DeviceLicenseEntity;
import entity.LicenseEntity;
import entity.DeviceEntity;
import enums.DeviceLicenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceLicenseRepository extends JpaRepository<DeviceLicenseEntity, UUID> {
    long countByLicenseAndStatus(LicenseEntity license, DeviceLicenseStatus status);

    Optional<DeviceLicenseEntity> findByLicenseAndDeviceAndStatus(LicenseEntity license, DeviceEntity device, DeviceLicenseStatus status);
    Optional<DeviceLicenseEntity> findFirstByLicenseAndStatus(LicenseEntity license, DeviceLicenseStatus status);
}