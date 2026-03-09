package model;

import enums.LicenseStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LicenseHistory {
    private UUID id;
    private UUID licenseId;
    private String licenseCode;
    private LicenseStatus status;
    private String userName;
    private UUID userId;
    private LocalDateTime timestamp;
    private String description;
}