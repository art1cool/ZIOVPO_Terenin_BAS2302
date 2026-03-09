package model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LicenseResponse {
    private UUID id;
    private String code;
    private String productName;
    private String licenseTypeName;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean blocked;
    private int deviceCount;
    private LocalDateTime firstActivationDate;
    private String description;
}