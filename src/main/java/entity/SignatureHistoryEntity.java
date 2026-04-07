package entity;

import enums.SignatureStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "signatures_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(name = "signature_id", nullable = false)
    private UUID signatureId;

    @Column(name = "version_created_at", nullable = false)
    private Instant versionCreatedAt;

    private String threatName;
    private String firstBytesHex;
    private String remainderHashHex;
    private Long remainderLength;
    private String fileType;
    private Long offsetStart;
    private Long offsetEnd;
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private SignatureStatus status;

    @Column(columnDefinition = "TEXT")
    private String digitalSignatureBase64;
}