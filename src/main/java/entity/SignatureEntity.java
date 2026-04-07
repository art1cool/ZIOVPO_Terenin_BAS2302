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
@Table(name = "signatures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String threatName;

    @Column(name = "first_bytes_hex", nullable = false)
    private String firstBytesHex;

    @Column(name = "remainder_hash_hex", nullable = false)
    private String remainderHashHex;

    @Column(name = "remainder_length", nullable = false)
    private long remainderLength;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "offset_start", nullable = false)
    private long offsetStart;

    @Column(name = "offset_end", nullable = false)
    private long offsetEnd;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignatureStatus status;

    @Column(columnDefinition = "TEXT")
    private String digitalSignatureBase64;
}