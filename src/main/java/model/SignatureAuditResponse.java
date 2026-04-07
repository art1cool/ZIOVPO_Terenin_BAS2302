package model;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class SignatureAuditResponse {
    private Long auditId;
    private UUID signatureId;
    private String changedBy;
    private Instant changedAt;
    private String fieldsChanged;
    private String description;
}