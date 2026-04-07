package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "signatures_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column(name = "signature_id", nullable = false)
    private UUID signatureId;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "fields_changed", columnDefinition = "TEXT")
    private String fieldsChanged;

    private String description;
}