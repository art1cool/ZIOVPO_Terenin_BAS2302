package model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateLicenseRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private UUID typeId;

    @NotNull
    private UUID ownerId;
}