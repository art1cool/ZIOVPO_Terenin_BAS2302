package model;

import lombok.Data;
import java.util.UUID;

@Data
public class LicenseType {
    private UUID id;
    private String name;
    private String description;
    private Integer durationDays;
}
