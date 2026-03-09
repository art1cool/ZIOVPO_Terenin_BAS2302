package model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LicenseVerificationResponse {
    private boolean valid;
    private String message;
    private UUID licenseId;
    private String licenseCode;
    private String productName;
    private LocalDateTime expiresAt;
    private boolean blocked;
    private int activeDevices;
    private int maxDevices;
}