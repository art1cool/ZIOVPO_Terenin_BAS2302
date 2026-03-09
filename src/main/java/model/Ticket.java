package model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Ticket {
    private LocalDateTime serverTime;
    private long ticketLifetimeSeconds;
    private LocalDateTime activationDate;
    private LocalDateTime expiresAt;
    private UUID userId;
    private String deviceIdentifier;
    private boolean blocked;
    private UUID licenseId;
    private String productName;
}