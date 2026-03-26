package service;

import controller.GlobalExceptionHandler;
import entity.LicenseTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.LicenseTypeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseTypeService {
    private final LicenseTypeRepository licenseTypeRepository;

    public LicenseTypeEntity getTypeOrFail(UUID typeId) {
        return licenseTypeRepository.findById(typeId)
                .orElseThrow(() -> new GlobalExceptionHandler.ResourceNotFoundException("License type not found with id: " + typeId));
    }
}