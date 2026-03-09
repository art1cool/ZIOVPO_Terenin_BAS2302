package model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RenewLicenseRequest {
    @NotBlank
    private String activationKey;
}