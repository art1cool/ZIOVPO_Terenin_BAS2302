package model;

import enums.SignatureStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SignatureResponse {
    private UUID id;
    private String threatName;
    private String firstBytesHex;
    private String remainderHashHex;
    private long remainderLength;
    private String fileType;
    private long offsetStart;
    private long offsetEnd;
    private Instant updatedAt;
    private SignatureStatus status;
    private String digitalSignatureBase64;
}