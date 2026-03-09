package model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActivateLicenseRequest {
    @NotBlank
    private String activationKey;

    @NotBlank
    private String deviceIdentifier;

    private String deviceName;
}