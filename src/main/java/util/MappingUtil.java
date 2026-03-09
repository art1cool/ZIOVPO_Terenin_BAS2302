package util;

import entity.*;
import model.*;
import org.springframework.stereotype.Component;

@Component
public class MappingUtil {

    public Product toDto(ProductEntity entity) {
        Product dto = new Product();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBlocked(entity.isBlocked());
        return dto;
    }

    public ProductEntity toEntity(Product dto) {
        ProductEntity entity = new ProductEntity();
        entity.setName(dto.getName());
        entity.setBlocked(dto.isBlocked());
        return entity;
    }

    public LicenseType toDto(LicenseTypeEntity entity) {
        LicenseType dto = new LicenseType();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setDurationDays(entity.getDurationDays());
        return dto;
    }

    public LicenseTypeEntity toEntity(LicenseType dto) {
        LicenseTypeEntity entity = new LicenseTypeEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDurationDays(dto.getDurationDays());
        return entity;
    }

    public LicenseResponse toDto(LicenseEntity entity) {
        LicenseResponse dto = new LicenseResponse();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setProductName(entity.getProduct().getName());
        dto.setLicenseTypeName(entity.getLicenseType().getName());
        dto.setOwnerName(entity.getOwner().getName());
        dto.setDescription(entity.getDescription());
        dto.setDeviceCount(entity.getDeviceCount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setFirstActivationDate(entity.getFirstActivationDate());
        dto.setBlocked(entity.isBlocked());
        return dto;
    }

    public LicenseEntity toEntity(CreateLicenseRequest request) {
        LicenseEntity entity = new LicenseEntity();
        return entity;
    }

    public LicenseHistory toDto(LicenseHistoryEntity entity) {
        LicenseHistory dto = new LicenseHistory();
        dto.setId(entity.getId());
        dto.setLicenseId(entity.getLicense().getId());
        dto.setLicenseCode(entity.getLicense().getCode());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setUserName(entity.getUser().getName());
        dto.setUserId(entity.getUser().getId());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }

    public User toDto(UserEntity entity) {
        User dto = new User();
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    public UserEntity toEntity(User dto) {
        UserEntity entity = new UserEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        return entity;
    }
}